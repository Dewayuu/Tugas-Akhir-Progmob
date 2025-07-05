package com.example.tugasakhirprogmob.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.tugasakhirprogmob.BuildConfig
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.auth.FirebaseAuth



// Data class yang dipetakan ke dokumen di Firestore
// Properti dibuat nullable agar bisa di-map dari Firestore dengan aman
data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val brand: String = "",
    // Untuk data baru (mendukung banyak gambar)
    val imageUrls: List<String> = emptyList(),
    // Untuk data lama (hanya satu gambar), dibuat nullable
    val imageUrl: String? = null,
    val sellerId: String = "",
    val sellerName: String = "",
    val postedAt: Timestamp? = null
)

// Data class untuk request penambahan produk baru
// Ini memisahkan model untuk 'create' dari model untuk 'read'
data class ProductRequest(
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val brand: String,
    val imageUrls: List<String>,
    val sellerId: String,
    val sellerName: String,
    val postedAt: FieldValue = FieldValue.serverTimestamp()
)

class ProductViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // Variabel untuk menyimpan listener agar bisa dilepas saat tidak dibutuhkan
    private var userProductsListener: ListenerRegistration? = null
    private var allProductsListener: ListenerRegistration? = null

    // Listener untuk status autentikasi
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            // Jika user login, pasang listener untuk produknya
            listenForUserProducts(user.uid)
        } else {
            // Jika user logout, hentikan listener dan kosongkan data
            userProductsListener?.remove()
            _userProducts.value = emptyList()
        }
    }

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    // State khusus untuk produk milik user
    private val _userProducts = MutableStateFlow<List<Product>>(emptyList())
    val userProducts: StateFlow<List<Product>> = _userProducts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    init {
        // Panggil listener saat ViewModel pertama kali dibuat
        // Ini memastikan data user dan semua produk selalu up-to-date
        listenForAllProducts()
        auth.addAuthStateListener(authStateListener)
    }


    private fun listenForUserProducts(userId: String) {
        // Hentikan listener lama jika ada, untuk menghindari duplikat
        userProductsListener?.remove()
        userProductsListener = db.collection("products")
            .whereEqualTo("sellerId", userId)
            .orderBy("postedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ProductViewModel", "Error listening for user products", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val userProductList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Product::class.java)?.copy(id = doc.id)
                    }
                    _userProducts.value = userProductList
                }
            }
    }


    private fun listenForAllProducts() {
        allProductsListener?.remove()
        allProductsListener = db.collection("products")
            .orderBy("postedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ProductViewModel", "Error listening for all products", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val allProductList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Product::class.java)?.copy(id = doc.id)
                    }
                    _products.value = allProductList
                }
            }
    }


    // Fungsi untuk menghapus produk
    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                db.collection("products").document(productId).delete().await()
                Log.d("ProductViewModel", "Product $productId deleted successfully.")
                // Setelah berhasil hapus, ambil ulang daftar produk user
//                fetchUserProducts()
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error deleting product $productId", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProduct(
        context: Context,
        name: String,
        priceStr: String,
        brand: String,
        category: String,
        description: String,
        imageUris: List<Uri> // Diubah menjadi list
    ) {
        if (name.isBlank() || priceStr.isBlank() || imageUris.isEmpty()) {
            Log.e("ProductViewModel", "Validation failed: Missing fields.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _isSuccess.value = false

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e("ProductViewModel", "User not logged in, cannot add product.")
                _isLoading.value = false
                return@launch
            }

            try {
                // Inisialisasi Cloudinary (aman untuk dipanggil berulang kali)
                CloudinaryManager.init(context.applicationContext)

                // Unggah semua gambar secara bersamaan dan kumpulkan URL-nya
                val uploadedImageUrls = coroutineScope {
                    imageUris.map { uri ->
                        async(Dispatchers.IO) {
                            val compressedData = compressImage(context, uri)
                            uploadImageToCloudinary(compressedData)
                        }
                    }.awaitAll() // Tunggu semua proses async selesai
                }

                // Cek jika ada unggahan yang gagal
                if (uploadedImageUrls.any { it.isEmpty() }) {
                    throw Exception("One or more image uploads failed.")
                }

                val newProduct = ProductRequest(
                    name = name,
                    price = priceStr.toDoubleOrNull() ?: 0.0,
                    brand = brand,
                    category = category,
                    description = description,
                    imageUrls = uploadedImageUrls, // Simpan list URL
                    sellerId = currentUser.uid,
                    sellerName = currentUser.displayName.orEmpty()
                )

                db.collection("products").add(newProduct).await()

                Log.d("ProductViewModel", "Product added successfully to Firestore.")
                _isSuccess.value = true
                // Setelah berhasil tambah, ambil ulang daftar produk user
//                fetchUserProducts()
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error adding product", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun compressImage(context: Context, imageUri: Uri): ByteArray {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            var quality = 90
            val outputStream = ByteArrayOutputStream()

            // Kompres gambar ke dalam format JPEG
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            // Jika ukurannya masih terlalu besar (misal > 1MB), kurangi kualitasnya lagi
            // Batas ini bisa disesuaikan
            while (outputStream.size() > 1_000_000 && quality > 10) {
                outputStream.reset() // Hapus data kompresi sebelumnya
                quality -= 10 // Kurangi kualitas
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                Log.d("ImageCompress", "Retrying compression with quality: $quality")
            }

            Log.d("ImageCompress", "Final image size: ${outputStream.size() / 1024} KB with quality $quality%")
            outputStream.toByteArray()
        }
    }

    private suspend fun uploadImageToCloudinary(imageData: ByteArray): String {
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(imageData) // Unggah data byte, bukan URI
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d("CloudinaryUpload", "Upload started...")
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val url = resultData?.get("secure_url") as? String
                        if (url != null) {
                            continuation.resume(url)
                        } else {
                            continuation.resumeWithException(Exception("Cloudinary URL is null"))
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("CloudinaryUpload", "Upload error: ${error?.description}")
                        continuation.resumeWithException(Exception(error?.description ?: "Unknown Cloudinary error"))
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        }
    }

    fun resetSuccessState() {
        _isSuccess.value = false
    }

    override fun onCleared() {
        super.onCleared()
        userProductsListener?.remove()
        allProductsListener?.remove()
        auth.removeAuthStateListener(authStateListener)
    }
}