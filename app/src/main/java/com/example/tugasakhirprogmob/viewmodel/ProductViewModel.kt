package com.example.tugasakhirprogmob.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

// Data class yang akan dipetakan ke dokumen di Firestore
data class Product(
    val id: String? = null,
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val brand: String = "",
    val imageUrl: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val postedAt: FieldValue? = null
)

class ProductViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    // State untuk menampung daftar produk dari Firestore
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    // State untuk menunjukkan status loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State untuk menunjukkan status selesai (misal: setelah berhasil menambah produk)
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    // Dipanggil untuk mengambil semua produk (misal: untuk HomePage.kt)
    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("products").get().await()
                _products.value = snapshot.toObjects(Product::class.java)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error fetching products", e)
                // Handle error (misal: tampilkan pesan)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi ini akan dipanggil dari ProductCreatePage.kt saat tombol "Add Listing" ditekan
    fun addProduct(
        name: String,
        priceStr: String,
        brand: String,
        category: String,
        description: String,
        imageUri: Uri?
    ) {
        // Validasi input dasar
        if (name.isBlank() || priceStr.isBlank() || imageUri == null) {
            // TODO: Tampilkan pesan error ke pengguna
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _isSuccess.value = false

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e("ProductViewModel", "User not logged in")
                _isLoading.value = false
                return@launch
            }

            try {
                // 1. Unggah gambar ke Firebase Storage
                val imageUrl = uploadProductImage(imageUri)

                // 2. Siapkan objek produk untuk disimpan
                val product = Product(
                    name = name,
                    price = priceStr.toDoubleOrNull() ?: 0.0,
                    brand = brand,
                    category = category,
                    description = description,
                    imageUrl = imageUrl,
                    sellerId = currentUser.uid,
                    sellerName = currentUser.displayName ?: "Anonymous", // Ambil nama pengguna jika ada
                    postedAt = FieldValue.serverTimestamp() // Gunakan timestamp server
                )

                // 3. Simpan objek produk ke Firestore
                db.collection("products").add(product).await()

                Log.d("ProductViewModel", "Product added successfully!")
                _isSuccess.value = true // Tandai berhasil

            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error adding product", e)
                // TODO: Tampilkan pesan error ke pengguna
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi helper untuk mengunggah gambar dan mendapatkan URL-nya
    private suspend fun uploadProductImage(uri: Uri): String {
        return try {
            val fileName = "products/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(fileName)
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("ProductViewModel", "Error uploading image", e)
            "" // Kembalikan string kosong jika gagal
        }
    }

    fun resetSuccessState() {
        _isSuccess.value = false
    }
}
