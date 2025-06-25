package com.example.tugasakhirprogmob

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tugasakhirprogmob.ui.components.BottomNavBar
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
// Import Product data class dari viewmodel
import com.example.tugasakhirprogmob.viewmodel.Product
import com.example.tugasakhirprogmob.viewmodel.SearchUiState
import com.example.tugasakhirprogmob.viewmodel.SearchViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage


// Anda bisa memanggil SearchScreen dari NavHost di MainApp
// composable("search") { SearchScreen(navController = navController) }

@Composable
fun SearchScreen(
    navController: NavController,
//    viewModel: SearchViewModel = viewModel(),
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSearchFocusChange: () -> Unit
) {
    // Ambil state dan fungsi dari ViewModel
//    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            SearchTopBar(
                query = uiState.searchQuery,
                onQueryChange = onQueryChange,
                onSearch = { query ->
                    onSearch(query)
                    keyboardController?.hide()
                },
                onFocusChange = onSearchFocusChange
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.isSearching) {
                SearchHistoryView(
                    history = uiState.searchHistory,
                    onHistoryClick = { historyTerm ->
                        onQueryChange(historyTerm)
                        onSearch(historyTerm)
                        keyboardController?.hide()
                    }
                )
            } else {
                SearchResultsHeader(query = uiState.searchQuery)
                // ProductGrid sekarang menerima List<Product> dari ViewModel
                ProductGrid(
                    products = uiState.displayedProducts
                )
            }
        }
    }
}

@Composable
fun ProductGrid(products: List<Product>, modifier: Modifier = Modifier) {
    if (products.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No products found.")
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products, key = { it.id }) { product ->
            // Gunakan ProductCard yang sudah ada dan konsisten
            HomePageProductCard(product = product)
        }
    }
}

// Gunakan kembali ProductCard yang sama seperti di HomePage untuk konsistensi
// Saya beri nama berbeda di sini untuk menghindari konflik jika ada file lain
@Composable
fun HomePageProductCard(product: Product) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }

    Column(modifier = Modifier.clickable { /* TODO: Navigasi ke detail produk dengan ID product.id */ }) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(product.brand, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(product.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Text(formatCurrency.format(product.price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}


// --- Composable lainnya seperti SearchTopBar, SearchResultsHeader, SearchHistoryView tetap sama ---

@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onFocusChange: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(color = Color.White, shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Cari Jaket, Parka...") },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onFocusChange()
                        }
                    },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Search Icon"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    onSearch(query)
                }),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.DarkGray
                )
            )
        }
    }
}

@Composable
fun SearchHistoryView(history: List<String>, onHistoryClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
        item {
            Text(
                text = "Riwayat Pencarian",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(history) { term ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHistoryClick(term) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History Icon",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = term, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun SearchResultsHeader(query: String) {
    val headerText = if (query.isBlank()) {
        "Semua Produk"
    } else {
        "Hasil untuk \"$query\""
    }
    Text(
        text = headerText,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}


@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    TugasAkhirProgmobTheme {
        val dummyNavController = rememberNavController()

        // Buat data bohongan untuk preview
        val dummyUiState = SearchUiState(
            displayedProducts = emptyList(), // atau isi dengan data produk palsu
            isSearching = false,
            isLoading = false
        )

        SearchScreen(
            navController = dummyNavController,
            uiState = dummyUiState,
            onQueryChange = {},
            onSearch = {},
            onSearchFocusChange = {}
        )
    }
}
