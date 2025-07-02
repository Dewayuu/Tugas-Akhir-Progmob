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

    // Fungsi untuk mengambil detail satu produk berdasarkan ID
    fun fetchProductById(productId: String) {
        // Jangan ambil lagi jika produk sudah ada atau sedang loading
        if (_product.value?.id == productId || _isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val document = db.collection("products").document(productId).get().await()
                if (document.exists()) {
                    // Konversi dokumen Firestore ke dalam data class Product kita
                    _product.value = document.toObject<Product>()?.copy(id = document.id)
                    Log.d("ProductDetailVM", "Product fetched: ${_product.value?.name}")
                } else {
                    Log.w("ProductDetailVM", "Product with ID $productId not found.")
                    _product.value = null
                }
            } catch (e: Exception) {
                Log.e("ProductDetailVM", "Error fetching product details", e)
                _product.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
