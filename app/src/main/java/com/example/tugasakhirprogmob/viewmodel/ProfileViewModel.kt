package com.example.tugasakhirprogmob.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.google.firebase.firestore.FieldValue


// Data class untuk menampung data profil yang akan ditampilkan di UI
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val bio: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null,
    val bannerUrl: String? = null
)

class ProfileViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    // Mengambil data profil pengguna yang sedang login
    fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = db.collection("users").document(userId).get().await()
                _userProfile.value = doc.toObject<UserProfile>()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching user profile", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Memperbarui data profil
    fun updateProfile(
        context: Context,
        newName: String,
        newBio: String,
        newAddress: String,
        newPhoneNumber: String,
        newProfilePicUri: Uri?,
        newBannerUri: Uri?,
        removeProfilePic: Boolean,
        removeBanner: Boolean
    ) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _updateSuccess.value = false

            try {
                // Siapkan map untuk menampung data yang akan diupdate
                val updates = mutableMapOf<String, Any>()

                if (removeProfilePic) {
                    updates["profilePictureUrl"] = FieldValue.delete()
                } else if (newProfilePicUri != null) {
                    val imageUrl = uploadImageToCloudinary(context, newProfilePicUri, "profile_pictures")
                    updates["profilePictureUrl"] = imageUrl
                }

                // 1. Cek dan unggah foto profil jika ada yang baru
                if (newProfilePicUri != null) {
                    val imageUrl = uploadImageToCloudinary(context, newProfilePicUri, "profile_pictures")
                    updates["profilePictureUrl"] = imageUrl
                }

                // 2. Cek dan unggah banner jika ada yang baru
                if (removeBanner) {
                    updates["bannerUrl"] = FieldValue.delete()
                } else if (newBannerUri != null) {
                    val bannerUrl = uploadImageToCloudinary(context, newBannerUri, "banners")
                    updates["bannerUrl"] = bannerUrl
                }

                // 3. Tambahkan data teks ke dalam map
                updates["name"] = newName
                updates["bio"] = newBio
                updates["address"] = newAddress
                updates["phoneNumber"] = newPhoneNumber

                // 4. Lakukan update ke Firestore
                if (updates.isNotEmpty()) {
                    db.collection("users").document(userId).update(updates).await()
                }
                _updateSuccess.value = true

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi helper untuk unggah gambar ke Cloudinary
    private suspend fun uploadImageToCloudinary(context: Context, imageUri: Uri, folder: String): String {
        // Inisialisasi Cloudinary jika belum
        CloudinaryManager.init(context.applicationContext)

        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(imageUri)
                .option("folder", folder) // Simpan di folder yang ditentukan
                .callback(object : UploadCallback {
                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val url = resultData?.get("secure_url") as? String
                        if (url != null) {
                            continuation.resume(url)
                        } else {
                            continuation.resumeWithException(Exception("Cloudinary URL is null"))
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        continuation.resumeWithException(Exception(error?.description ?: "Unknown Cloudinary error"))
                    }
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        }
    }

    fun resetUpdateStatus() {
        _updateSuccess.value = false
    }
}
