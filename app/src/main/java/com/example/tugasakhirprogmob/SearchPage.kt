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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tugasakhirprogmob.ui.components.BottomNavBar
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.viewmodel.Product
import com.example.tugasakhirprogmob.viewmodel.SearchUiState
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage


@Composable
fun SearchScreen(
    navController: NavController,
    uiState: SearchUiState,
    allProducts: List<Product>, // Tetap terima daftar produk
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSearchFocusChange: () -> Unit,
    onScreenVisible: () -> Unit
) {
    LaunchedEffect(Unit) {
        onScreenVisible()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    // --- KEMBALIKAN STRUKTUR SCAFFOLD ---
    Scaffold(
        topBar = {
            Column (
                modifier = Modifier.statusBarsPadding()
            ) {
                SearchTopBar(
                    query = uiState.searchQuery,
                    onQueryChange = onQueryChange,
                    onSearch = { query ->
                        onSearch(query)
                        keyboardController?.hide()
                    },
                    onFocusChange = onSearchFocusChange,
                    onCartClick = { navController.navigate(Screen.Cart.route) }
                )
            }
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
                ProductGrid(
                    products = uiState.displayedProducts,
                    navController = navController
                )
            }
        }
    }
}


@Composable
fun ProductGrid(products: List<Product>, navController: NavController, modifier: Modifier = Modifier) {
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
            HomePageProductCard(product = product, navController = navController)
        }
    }
}

@Composable
fun HomePageProductCard(product: Product, navController: NavController) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }

    Column(modifier = Modifier.clickable {
        navController.navigate("productDetail/${product.id}")
    }) {
        val displayImage = if (product.imageUrls.isNotEmpty()) {
            product.imageUrls.first()
        } else {
            product.imageUrl
        }
        AsyncImage(
            model = displayImage,
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

@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onFocusChange: () -> Unit,
    onCartClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search Edge...") },
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onFocusChange()
                    }
                },
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search Icon",
                    modifier = Modifier.size(26.dp)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
                onSearch(query)
            }),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color.White,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onCartClick) {
            Icon(
                painter = painterResource(id = R.drawable.cart),
                contentDescription = "Cart",
                modifier = Modifier.size(28.dp)
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