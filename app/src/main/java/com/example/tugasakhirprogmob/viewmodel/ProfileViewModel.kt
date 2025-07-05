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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
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

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    // --- TAMBAHKAN STATE BARU INI ---
    private val _paymentSuccess = MutableStateFlow(false)
    val paymentSuccess: StateFlow<Boolean> = _paymentSuccess
    // ---------------------------------

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

    fun fetchUserOrders() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val ordersSnapshot = db.collection("orders")
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                val orderList = ordersSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                }
                _orders.value = orderList
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching user orders", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- TAMBAHKAN FUNGSI BARU INI ---
    fun confirmPayment(orderId: String) {
        viewModelScope.launch {
            try {
                db.collection("orders").document(orderId)
                    .update("status", "Lunas")
                    .await()
                _paymentSuccess.value = true
                // Refresh daftar pesanan setelah update
                fetchUserOrders()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error confirming payment", e)
            }
        }
    }

    fun resetPaymentStatus() {
        _paymentSuccess.value = false
    }
    // ---------------------------------

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
                val updates = mutableMapOf<String, Any>()
                if (removeProfilePic) {
                    updates["profilePictureUrl"] = FieldValue.delete()
                } else if (newProfilePicUri != null) {
                    val imageUrl = uploadImageToCloudinary(context, newProfilePicUri, "profile_pictures")
                    updates["profilePictureUrl"] = imageUrl
                }
                if (removeBanner) {
                    updates["bannerUrl"] = FieldValue.delete()
                } else if (newBannerUri != null) {
                    val bannerUrl = uploadImageToCloudinary(context, newBannerUri, "banners")
                    updates["bannerUrl"] = bannerUrl
                }
                updates["name"] = newName
                updates["bio"] = newBio
                updates["address"] = newAddress
                updates["phoneNumber"] = newPhoneNumber
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

    private suspend fun uploadImageToCloudinary(context: Context, imageUri: Uri, folder: String): String {
        CloudinaryManager.init(context.applicationContext)
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(imageUri)
                .option("folder", folder)
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