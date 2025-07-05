package com.example.tugasakhirprogmob

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.ui.components.BottomNavBar
import com.example.tugasakhirprogmob.ui.components.TopBar
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.viewmodel.Product
import com.example.tugasakhirprogmob.viewmodel.ProductViewModel
import com.example.tugasakhirprogmob.viewmodel.ProfileViewModel
import com.example.tugasakhirprogmob.viewmodel.SearchViewModel
import java.text.NumberFormat
import java.util.Locale



// Wrapper utama aplikasi dengan Navigasi.
// Ini bisa berada di MainActivity.kt atau di sini.
// Kita tidak mengubah bagian ini, sesuai permintaan Anda.
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val searchViewModel: SearchViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                // Teruskan instance yang sama jika HomeScreen membutuhkannya
                productViewModel = productViewModel,
                searchViewModel = searchViewModel
            )
        }
        composable(Screen.Add.route) {
            ProductCreateScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                // --- PERBAIKAN: Teruskan instance ViewModel yang sudah dibagikan ---
                productViewModel = productViewModel
            )
        }
        composable(Screen.SearchScreen.route) {
            val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
            val allProducts by productViewModel.products.collectAsStateWithLifecycle()

            SearchScreen(
                navController = navController,
                uiState = uiState,
                // Teruskan daftar produk ke SearchScreen
                allProducts = allProducts,
                onQueryChange = searchViewModel::onSearchQueryChanged,
                // Saat mencari, berikan query dan daftar produknya
                onSearch = { query ->
                    searchViewModel.executeSearch(query, allProducts)
                },
                onSearchFocusChange = searchViewModel::onSearchFocused,
                // Saat layar muncul, reset state dengan daftar produk terbaru
                onScreenVisible = {
                    searchViewModel.resetSearchState(allProducts)
                }
            )
        }
        composable(Screen.Cart.route) {
            ViewCartScreen(
                navController = navController, // Teruskan navController
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            UserProfileScreen(
                navController = navController,
                onCartClick = { navController.navigate(Screen.Cart.route) },
                // --- PERBAIKAN: Teruskan instance ViewModel yang sudah dibagikan ---
                productViewModel = productViewModel,
                searchViewModel = searchViewModel,
                profileViewModel = profileViewModel
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                profileViewModel = profileViewModel
            )
        }

        composable("checkout") {
            CheckoutScreen(navController = navController)
        }

        composable(
            route = "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            if (productId != null) {
                ProductDetailScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}


@Composable
fun HomeScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    searchViewModel: SearchViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // --- STATE DARI VIEWMODEL (TIDAK BERUBAH) ---
    val realProducts by productViewModel.products.collectAsStateWithLifecycle()
    val isLoading by productViewModel.isLoading.collectAsStateWithLifecycle()
    val searchUiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    // --- LOGIKA UNTUK FITUR PENCARIAN (TIDAK BERUBAH) ---
    // Logika ini sudah benar dan dipertahankan.
    var searchQuery by remember { mutableStateOf("") }
    var isSearchBarFocused by remember { mutableStateOf(false) }
    // var searchHistory by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var searchExecuted by remember { mutableStateOf(false) }

    val displayedProducts = if (searchExecuted) {
        realProducts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.brand.contains(searchQuery, ignoreCase = true)
        }
    } else {
        realProducts
    }

    fun performSearch(query: String) {
        val trimmedQuery = query.trim()
        keyboardController?.hide()
        focusManager.clearFocus()

        // Delegasikan proses pencarian & penyimpanan riwayat ke ViewModel
        searchViewModel.executeSearch(trimmedQuery, realProducts)

        // Tetap kelola state lokal untuk mengontrol UI di HomePage
        searchQuery = trimmedQuery
        searchExecuted = trimmedQuery.isNotBlank()
        isSearchBarFocused = false
    }
    // --- AKHIR DARI LOGIKA PENCARIAN ---

//    LaunchedEffect(Unit) {
//        productViewModel.fetchProducts()
//    }

    Scaffold(
        topBar = {
            Column (
                modifier = Modifier.statusBarsPadding()
            ) {
                TopBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        searchViewModel.onSearchQueryChanged(it)
                        if (it.isBlank()) {
                            searchExecuted = false
                        }
                    },
                    onSearch = { performSearch(it) },
                    onFocusChange = { isFocused -> isSearchBarFocused = isFocused },
                    onCartClick = { navController.navigate(Screen.Cart.route) }
                )
            }
        },
        bottomBar = {
            if (!isSearchBarFocused && !searchExecuted) {
                // --- MENGGUNAKAN BottomNavBar DARI SHARED COMPOSABLES ---
                BottomNavBar(navController = navController)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && realProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            // --- LOGIKA TAMPILAN DINAMIS (MENGGUNAKAN SHARED COMPOSABLES) ---
            else if (isSearchBarFocused) {
                SearchHistoryView(
                    history = searchUiState.searchHistory,
                    onHistoryClick = { historyTerm ->
                        searchQuery = historyTerm // Update text di search bar
                        performSearch(historyTerm)
                    }
                )
            } else if (searchExecuted) {
                SearchResultsUI(
                    query = searchQuery,
                    products = displayedProducts,
                    navController = navController,
                    onDismiss = {
                        focusManager.clearFocus()
                        searchQuery = ""
                        searchExecuted = false
                    }
                )
            } else {
                // Tampilan default homepage
                DefaultHomeScreenContent(products = realProducts, navController = navController)
            }
        }
    }
}


// --- KONTEN-KONTEN SCREEN ---
// Composable di bawah ini spesifik untuk HomePage, jadi tetap di sini.
// Namun, sekarang ia memanggil ProductCard dari SharedComposables.

@Composable
fun DefaultHomeScreenContent(products: List<Product>, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        item { TopSellingBanner() }
        item { CategoryRow() }
        item {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Newest", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(">", Modifier.clickable { /* TODO */ })
            }
        }
        items(products.chunked(2)) { productRow ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                productRow.forEach { product ->
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(product = product, navController = navController)
                    }
                }
                if (productRow.size == 1) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SearchResultsUI(query: String, products: List<Product>, navController: NavController, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        // --- MENGGUNAKAN SearchResultsHeader DARI SHARED COMPOSABLES ---
        SearchResultsHeader(query = query)

        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Not Found",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Product not found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Try using different keywords.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(products.chunked(2)) { productRow ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        productRow.forEach { product ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(product = product, navController = navController)
                            }
                        }
                        if (productRow.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// ProductCard perlu dimodifikasi untuk menerima NavController agar bisa navigasi ke detail
@Composable
fun ProductCard(product: Product, navController: NavController) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    Column(modifier = Modifier.clickable {
        // Aksi: Navigasi ke halaman detail dengan mengirimkan ID produk
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
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)).background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(product.brand, style = MaterialTheme.typography.labelSmall, color = Color.Gray, maxLines = 1)
        Text(product.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1)
        Text(formatCurrency.format(product.price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TopSellingBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.LightGray, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Top Selling Banner", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CategoryRow() {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Categories", fontWeight = FontWeight.Bold)
            Text(">", modifier = Modifier.align(Alignment.CenterVertically).clickable {})
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(5) { // Placeholder
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 16.dp).width(75.dp)
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).background(Color(0xFFEFEFEF), shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Icon bisa ditambahkan di sini
                    }
                    Text("Category $it")
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // SearchViewModel yang asli memanggil Firebase saat dibuat, yang akan crash di mode preview.
    // Kita buat ViewModel palsu (fake) yang tidak melakukan apa-apa khusus untuk preview.
    val fakeSearchViewModel = object : SearchViewModel() {
        // Meng-override init block agar tidak memanggil fetchAllProducts()
    }

    TugasAkhirProgmobTheme {
        val dummyNavController = rememberNavController()
        val fakeProductViewModel = ProductViewModel()
        HomeScreen(
            navController = dummyNavController,
            // Berikan ViewModel palsu yang sudah kita buat ke dalam parameter
            searchViewModel = fakeSearchViewModel,
            productViewModel = fakeProductViewModel
        )
    }
}
