package com.example.tugasakhirprogmob.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Data class untuk menampung seluruh state dari SearchScreen
data class SearchUiState(
    val searchQuery: String = "",
    val isSearching: Boolean = false, // True jika search bar sedang fokus
    val isLoading: Boolean = false,
    val displayedProducts: List<Product> = emptyList(),
    val searchHistory: List<String> = emptyList()
)

open class SearchViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Cache untuk menyimpan semua produk agar tidak perlu fetch berulang kali
    private var allProductsCache: List<Product> = emptyList()

    init {
        // Saat ViewModel dibuat, langsung ambil semua produk
        fetchAllProducts()
    }

    private fun fetchAllProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val snapshot = db.collection("products")
                    .orderBy("postedAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                allProductsCache = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }

                // Tampilkan semua produk saat pertama kali dibuka
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        displayedProducts = allProductsCache
                    )
                }
                Log.d("SearchViewModel", "Successfully fetched ${allProductsCache.size} products.")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error fetching all products", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Dipanggil saat teks di search bar berubah
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // Dipanggil saat search bar mendapatkan fokus
    fun onSearchFocused() {
        _uiState.update { it.copy(isSearching = true) }
    }

    // Dipanggil saat tombol search ditekan atau histori diklik
    fun executeSearch(query: String) {
        // Tambahkan query ke riwayat jika belum ada
        val currentHistory = _uiState.value.searchHistory.toMutableList()
        if (query.isNotBlank() && !currentHistory.contains(query)) {
            currentHistory.add(0, query)
        }

        // Lakukan filter di sisi klien (client-side)
        val filteredList = if (query.isBlank()) {
            allProductsCache // Jika query kosong, tampilkan semua produk
        } else {
            allProductsCache.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.brand.contains(query, ignoreCase = true) ||
                        product.category.contains(query, ignoreCase = true)
            }
        }

        _uiState.update {
            it.copy(
                isSearching = false, // Tutup tampilan histori
                displayedProducts = filteredList,
                searchHistory = currentHistory.take(10) // Batasi riwayat hingga 10 item
            )
        }
    }
    fun resetSearchState() {
        // Fungsi ini akan mengembalikan state ke kondisi awal
        _uiState.update {
            it.copy(
                searchQuery = "",
                displayedProducts = allProductsCache,
                isSearching = false // Juga reset tampilan histori/fokus
            )
        }
    }
}
