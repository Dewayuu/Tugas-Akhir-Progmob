package com.example.tugasakhirprogmob.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProductDetailViewModel : ViewModel() {

    private val db = Firebase.firestore

    // State untuk menampung detail produk yang berhasil diambil
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    // State untuk mengelola status loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State profil seller
    private val _sellerProfile = MutableStateFlow<UserProfile?>(null)
    val sellerProfile: StateFlow<UserProfile?> = _sellerProfile

    // Fungsi untuk mengambil detail satu produk berdasarkan ID

    // --- FUNGSI DIAMBIL ULANG DENGAN LOGIKA YANG LEBIH BAIK ---
    fun fetchProductById(productId: String) {
        if (_product.value?.id == productId || _isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ambil dokumen produk terlebih dahulu
                val productDoc = db.collection("products").document(productId).get().await()
                val productData = productDoc.toObject<Product>()?.copy(id = productDoc.id)

                if (productData != null) {
                    // Langsung set data produk agar UI bisa menampilkannya
                    _product.value = productData

                    // Kemudian, coba ambil profil penjual secara terpisah
                    val sellerId = productData.sellerId
                    if (sellerId.isNotBlank()) {
                        try {
                            val sellerDoc = db.collection("users").document(sellerId).get().await()
                            _sellerProfile.value = sellerDoc.toObject<UserProfile>()
                            Log.d("ProductDetailVM", "Seller fetched successfully.")
                        } catch (e: Exception) {
                            // Jika GAGAL mengambil seller, jangan set produk jadi null.
                            // Biarkan produk tetap tampil.
                            _sellerProfile.value = null
                            Log.e("ProductDetailVM", "Failed to fetch seller profile, but product will be shown.", e)
                        }
                    } else {
                        _sellerProfile.value = null
                        Log.w("ProductDetailVM", "Product fetched but sellerId is missing.")
                    }
                } else {
                    // Produk benar-benar tidak ditemukan
                    _product.value = null
                    _sellerProfile.value = null
                    Log.w("ProductDetailVM", "Product with ID $productId not found.")
                }
            } catch (e: Exception) {
                Log.e("ProductDetailVM", "Error fetching product details", e)
                _product.value = null
                _sellerProfile.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
    private fun fetchSellerProfile(sellerId: String) {
        viewModelScope.launch {
            try {
                val sellerDoc = db.collection("users").document(sellerId).get().await()
                _sellerProfile.value = sellerDoc.toObject<UserProfile>()
                Log.d("ProductDetailVM", "Seller profile fetched: ${_sellerProfile.value?.name}")
            } catch (e: Exception) {
                Log.e("ProductDetailVM", "Error fetching seller profile", e)
                _sellerProfile.value = null
            }
        }
    }
}
