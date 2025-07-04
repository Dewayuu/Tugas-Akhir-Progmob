package com.example.tugasakhirprogmob.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Data class untuk state (TIDAK BERUBAH)
data class SearchUiState(
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val isLoading: Boolean = false,
    val displayedProducts: List<Product> = emptyList(),
    val searchHistory: List<String> = emptyList()
)

open class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // --- AWAL PERUBAHAN ---
    // Semua logika fetching, cache, dan listener dari Firestore DIHAPUS.
    // ViewModel ini sekarang tidak mengambil datanya sendiri.

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSearchFocused() {
        _uiState.update { it.copy(isSearching = true) }
    }

    // Fungsi ini sekarang menerima daftar produk master untuk difilter
    fun executeSearch(query: String, allProducts: List<Product>) {
        val currentHistory = _uiState.value.searchHistory.toMutableList()
        if (query.isNotBlank() && !currentHistory.contains(query)) {
            currentHistory.add(0, query)
        }

        val filteredList = if (query.isBlank()) {
            allProducts // Jika query kosong, tampilkan semua produk dari master list
        } else {
            // Lakukan filter pada master list yang selalu update
            allProducts.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.brand.contains(query, ignoreCase = true) ||
                        product.category.contains(query, ignoreCase = true)
            }
        }

        _uiState.update {
            it.copy(
                isSearching = false,
                displayedProducts = filteredList,
                searchHistory = currentHistory.take(10)
            )
        }
    }

    // Fungsi ini juga menerima daftar produk master
    fun resetSearchState(allProducts: List<Product>) {
        _uiState.update {
            it.copy(
                searchQuery = "",
                displayedProducts = allProducts, // Saat direset, tampilkan semua produk lagi
                isSearching = false
            )
        }
    }
    // --- AKHIR PERUBAHAN ---
}