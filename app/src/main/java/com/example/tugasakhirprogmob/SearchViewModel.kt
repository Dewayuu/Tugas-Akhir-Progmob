package com.example.tugasakhirprogmob

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Data produk lengkap
val allProducts = listOf(
    Product("Nike", "Basic Jacket", "$10.99", R.drawable.nike),
    Product("Uniqlo", "Parka Hoodie", "$10.99", R.drawable.nike),
    Product("Adidas", "Windbreaker Jacket", "$25.50", R.drawable.nike),
    Product("Zara", "Leather Jacket", "$50.00", R.drawable.nike),
    Product("H&M", "Denim Jacket", "$30.99", R.drawable.nike),
    Product("Uniqlo", "Fleece Parka", "$19.99", R.drawable.nike),
    Product("Nike", "Sporty Parka", "$45.00", R.drawable.nike),
    Product("Puma", "Running Jacket", "$22.00", R.drawable.nike)
)

// State untuk UI
data class SearchUiState(
    val searchQuery: String = "",
    val displayedProducts: List<Product> = allProducts,
    val searchHistory: List<String> = emptyList(),
    val isSearching: Boolean = false // Untuk menentukan apakah menampilkan history atau hasil
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepository = SearchHistoryRepository(application)

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        // Muat riwayat pencarian saat ViewModel dibuat
        loadSearchHistory()
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(searchHistory = historyRepository.getSearchHistory()) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }


    fun executeSearch(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) {
            // Jika query kosong, tampilkan semua produk
            _uiState.update {
                it.copy(
                    displayedProducts = allProducts,
                    isSearching = false
                )
            }
            return
        }

        // Tambahkan ke riwayat
        historyRepository.addSearchTerm(trimmedQuery)
        loadSearchHistory() // Muat ulang riwayat agar UI terupdate

        // Lakukan filter produk
        val filteredProducts = allProducts.filter { product ->
            product.name.contains(trimmedQuery, ignoreCase = true) ||
                    product.brand.contains(trimmedQuery, ignoreCase = true)
        }

        _uiState.update {
            it.copy(
                displayedProducts = filteredProducts,
                isSearching = false // Selesai mencari, tampilkan hasil
            )
        }
    }

    // Dipanggil saat search bar mendapat fokus
    fun onSearchFocused() {
        _uiState.update { it.copy(isSearching = true) }
    }
}