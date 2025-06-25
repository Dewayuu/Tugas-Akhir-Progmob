package com.example.tugasakhirprogmob.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

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
    private val storage = Firebase.storage

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
                    .orderBy("postedAt", Query.Direction.DESCENDING) // Urutkan dari yang terbaru
                    .get()
                    .await()

                // Map dokumen ke data class, sertakan ID dokumen
                _products.value = snapshot.documents.mapNotNull { doc ->
                    val product = doc.toObject(Product::class.java)
                    product?.copy(id = doc.id) // Salin objek dan tambahkan ID-nya
                }
                Log.d("ProductViewModel", "Fetched ${_products.value.size} products.")
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error fetching products", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProduct(
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
                val imageUrl = uploadProductImage(imageUri)
                if(imageUrl.isEmpty()) {
                    throw Exception("Image upload failed.")
                }

                // Gunakan ProductRequest untuk membuat produk baru
                val newProduct = ProductRequest(
                    name = name,
                    price = priceStr.toDoubleOrNull() ?: 0.0,
                    brand = brand,
                    category = category,
                    description = description,
                    imageUrl = imageUrl,
                    sellerId = currentUser.uid,
                    // Ambil nama dari profil pengguna jika tersedia
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

    private suspend fun uploadProductImage(uri: Uri): String {
        return try {
            val fileName = "product_images/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(fileName)
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("ProductViewModel", "Error uploading image to Firebase Storage", e)
            ""
        }
    }

    fun resetSuccessState() {
        _isSuccess.value = false
    }
}
