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
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Data class yang dipetakan ke dokumen di Firestore
// Properti dibuat nullable agar bisa di-map dari Firestore dengan aman
data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val brand: String = "",
    val imageUrl: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val postedAt: Timestamp? = null // Ubah ke Timestamp agar bisa menerima data dari Firestore
)

// Data class untuk request penambahan produk baru
// Ini memisahkan model untuk 'create' dari model untuk 'read'
data class ProductRequest(
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val brand: String,
    val imageUrl: String,
    val sellerId: String,
    val sellerName: String,
    val postedAt: FieldValue = FieldValue.serverTimestamp()
)


class ProductViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    // Fungsi untuk mengambil semua produk, diurutkan dari yang terbaru
    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("products")
                    .orderBy("postedAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                _products.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error fetching products", e)
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
        imageUri: Uri?
    ) {
        if (name.isBlank() || priceStr.isBlank() || imageUri == null) {
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
                // Kompres gambar sebelum mengunggahnya
                val compressedImageData = compressImage(context, imageUri)

                // Unggah data gambar yang sudah dikompres
                val imageUrl = uploadImageToCloudinary(compressedImageData)

                val newProduct = ProductRequest(
                    name = name,
                    price = priceStr.toDoubleOrNull() ?: 0.0,
                    brand = brand,
                    category = category,
                    description = description,
                    imageUrl = imageUrl, // Simpan URL dari Cloudinary
                    sellerId = currentUser.uid,
                    sellerName = currentUser.displayName.takeIf { !it.isNullOrBlank() } ?: "Anonymous Seller"
                )

                db.collection("products").add(newProduct).await()

                Log.d("ProductViewModel", "Product added successfully to Firestore.")
                _isSuccess.value = true

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
}
