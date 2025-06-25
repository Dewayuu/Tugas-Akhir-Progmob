package com.example.tugasakhirprogmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import com.example.tugasakhirprogmob.ui.components.SearchHistoryView
import com.example.tugasakhirprogmob.ui.components.ProductCard
import androidx.navigation.NavController // Pastikan import ini ada
import androidx.navigation.compose.rememberNavController
import com.example.tugasakhirprogmob.ui.components.BottomNavBar

//class SearchPage : ComponentActivity() {
//    private val viewModel: SearchViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            TugasAkhirProgmobTheme {
//                // Ambil state dari ViewModel
//                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//                SearchScreen(
//                    uiState = uiState,
//                    onQueryChange = viewModel::onSearchQueryChanged,
//                    onSearch = viewModel::executeSearch,
//                    onSearchFocusChange = { viewModel.onSearchFocused() }
//                )
//            }
//        }
//    }
//}

// Menggunakan kembali data class yang sudah ada dari HomePage
// data class Product(val brand: String, val name: String, val price: String, val imageRes: Int)
// data class ChipData(val label: String, val imageRes: Int)

// Data sampel untuk halaman hasil pencarian
@Composable
fun SearchScreen(
    navController: NavController,
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSearchFocusChange: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            SearchTopBar(
                query = uiState.searchQuery,
                onQueryChange = onQueryChange,
                onSearch = {
                    onSearch(it)
                    keyboardController?.hide()
                },
                onFocusChange = onSearchFocusChange
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            FilterChips()

            if (uiState.isSearching) {
                // Tampilkan riwayat pencarian jika search bar aktif
                SearchHistoryView(
                    history = uiState.searchHistory,
                    onHistoryClick = { historyTerm ->
                        onQueryChange(historyTerm)
                        onSearch(historyTerm)
                        keyboardController?.hide()
                    }
                )
            } else {
                // Tampilkan hasil pencarian (atau semua produk)
                SearchResultsHeader(query = uiState.searchQuery)
                ProductGrid(
                    products = uiState.displayedProducts,
                )
            }
        }
    }
}

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

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = {
                // Arahkan ke halaman keranjang (nanti bisa pakai navController)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.cart),
                    contentDescription = "Shopping Cart",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}



//@Composable
//fun SearchHistoryView(history: List<String>, onHistoryClick: (String) -> Unit) {
//    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
//        item {
//            Text(
//                text = "Riwayat Pencarian",
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//        }
//        items(history) { term ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { onHistoryClick(term) }
//                    .padding(vertical = 12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.History, // atau Icons.Filled.History
//                    contentDescription = "History Icon",
//                    tint = Color.Gray
//                )
//                Spacer(modifier = Modifier.width(16.dp))
//                Text(text = term, style = MaterialTheme.typography.bodyLarge)
//            }
//        }
//    }
//}


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


// Anda bisa copy-paste fungsi FilterChips dari HomePage.kt ke sini
// atau letakkan di file terpisah agar bisa di-reuse.
//@Composable
//fun FilterChips() {
//    val chips = listOf(
//        ChipData("Filters", R.drawable.mage_filter),
//        ChipData("Jacket", R.drawable.jacket),
//        ChipData("Women's Clothing", R.drawable.t_shirt)
//    )
//
//    LazyRow(
//        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(chips) { chip ->
//            AssistChip(
//                onClick = {},
//                label = { Text(chip.label) },
//                leadingIcon = {
//                    Icon(
//                        painter = painterResource(id = chip.imageRes),
//                        contentDescription = null,
//                        modifier = Modifier.size(18.dp)
//                    )
//                }
//            )
//        }
//    }
//}

@Composable
fun ProductGrid(products: List<Product>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductCard(product = product)
        }
    }
}

//@Composable
//fun ProductCard(product: Product) {
//    // Menggunakan Surface lebih standar dan aman untuk membuat kartu
//    Surface(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp), // Jarak antar kartu
//        shape = RoundedCornerShape(12.dp),
//        border = BorderStroke(1.dp, Color(0xFFEEEEEE)) // Border yang sangat halus
//    ) {
//        Column(
//            // Padding ini ada DI DALAM kartu, memberi jarak antara border dan konten
//            modifier = Modifier.padding(12.dp)
//        ) {
//            Image(
//                painter = painterResource(id = product.imageRes),
//                contentDescription = product.name,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(1f)
//                    .clip(RoundedCornerShape(8.dp))
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = product.brand,
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.Gray
//            )
//            Text(
//                text = product.name,
//                style = MaterialTheme.typography.bodyLarge,
//                fontWeight = FontWeight.Normal
//            )
//            Text(
//                text = product.price,
//                style = MaterialTheme.typography.bodyLarge,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}

// Modifikasi BottomNavBar untuk menerima index item yang dipilih
//@Composable
//fun SearchBottomNavBar(selectedItem: Int) {
//    NavigationBar(
//        containerColor = Color.White
//    ) {
//        val navItems = listOf("Home", "Search", "Add", "Profile")
//        val navIcons = listOf(
//            Pair(R.drawable.home_outline, R.drawable.home_filled),
//            Pair(R.drawable.search2, R.drawable.search2),
//            Pair(R.drawable.add, R.drawable.add),
//            Pair(R.drawable.profile, R.drawable.profile)
//        )
//
//        navItems.forEachIndexed { index, label ->
//            NavigationBarItem(
//                // Logika `selected` sekarang langsung dari parameter, tanpa state lokal
//                selected = selectedItem == index,
//                // OnClick sebaiknya di-handle di level yang lebih tinggi, untuk saat ini kita kosongkan
//                onClick = { /* TODO: Implement navigation logic here */ },
//                icon = {
//                    Icon(
//                        painter = painterResource(
//                            id = if (selectedItem == index) navIcons[index].second else navIcons[index].first
//                        ),
//                        contentDescription = label,
//                        modifier = Modifier.size(24.dp)
//                    )
//                },
//                colors = NavigationBarItemDefaults.colors(
//                    indicatorColor = Color.Transparent,
//                    selectedIconColor = Color.Black,
//                    unselectedIconColor = Color.Gray
//                )
//            )
//        }
//    }
//}


@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    TugasAkhirProgmobTheme {
        // 1. Buat NavController bohongan untuk kebutuhan preview
        val dummyNavController = rememberNavController()

        // 2. Teruskan dummyNavController saat memanggil SearchScreen
        SearchScreen(
            navController = dummyNavController, // <-- TAMBAHKAN INI
            uiState = SearchUiState(searchQuery = "", displayedProducts = sampleProducts.take(4)), // saya ganti allProducts menjadi sampleProducts
            onQueryChange = {},
            onSearch = {},
            onSearchFocusChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchHistoryPreview() {
    TugasAkhirProgmobTheme {
        SearchHistoryView(history = listOf("Jacket", "Parka", "Hoodie"), onHistoryClick = {})
    }
}