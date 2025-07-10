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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.tugasakhirprogmob.ui.components.SearchHistoryView
import com.example.tugasakhirprogmob.ui.components.TopBar
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.viewmodel.Product
import com.example.tugasakhirprogmob.viewmodel.ProductViewModel
import com.example.tugasakhirprogmob.viewmodel.ProfileViewModel
import com.example.tugasakhirprogmob.viewmodel.SearchViewModel
import com.example.tugasakhirprogmob.AppConstants
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.TopAppBarDefaults

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
                productViewModel = productViewModel,
                searchViewModel = searchViewModel
            )
        }
        composable(Screen.Add.route) {
            ProductCreateScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                productViewModel = productViewModel,
                productId = null
            )
        }
        composable(Screen.SearchScreen.route) {
            val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
            val allProducts by productViewModel.products.collectAsStateWithLifecycle()

            SearchScreen(
                navController = navController,
                uiState = uiState,
                allProducts = allProducts,
                onQueryChange = searchViewModel::onSearchQueryChanged,
                onSearch = { query ->
                    searchViewModel.executeSearch(query, allProducts)
                },
                onSearchFocusChange = searchViewModel::onSearchFocused,
                onScreenVisible = {
                    searchViewModel.resetSearchState(allProducts)
                }
            )
        }
        composable(Screen.Cart.route) {
            ViewCartScreen(
                navController = navController,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Profile.route) {
            UserProfileScreen(
                navController = navController,
                onCartClick = { navController.navigate(Screen.Cart.route) },
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

        composable(
            "invoice/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            InvoiceScreen(
                navController = navController,
                orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            )
        }

        composable(
            "tracking/{orderId}", // Terima orderId
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            TrackingScreen(
                navController = navController,
                orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            )
        }

        composable(
            "order_detail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            OrderDetailScreen(
                navController = navController,
                orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            )
        }

        composable("checkout") {
            CheckoutScreen(
                navController = navController,
                cartViewModel = viewModel(), // Ambil instance CartViewModel
                profileViewModel = profileViewModel // Teruskan instance ProfileViewModel yang sudah ada
            )
        }

        composable("order_history") {
            OrderHistoryScreen(navController = navController)
        }

        composable(
            "payment_method/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            PaymentMethodScreen(
                navController = navController,
                orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            )
        }

        composable(
            "payment_instruction/{orderId}/{method}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType },
                navArgument("method") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            PaymentInstructionScreen(
                navController = navController,
                orderId = backStackEntry.arguments?.getString("orderId") ?: "",
                method = backStackEntry.arguments?.getString("method") ?: "",
                profileViewModel = profileViewModel
            )
        }

        composable("order_success") {
            OrderSuccessScreen(navController = navController)
        }


        composable(
            route = "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            if (productId != null) {
                ProductDetailScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() },
                    navController = navController
                )
            } else {
                navController.popBackStack()
            }
        }
        composable(
            route = "add_product_edit/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductCreateScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                productViewModel = productViewModel,
                productId = productId
            )
        }
        composable(
            route = "sellerProfile/{sellerId}",
            arguments = listOf(navArgument("sellerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sellerId = backStackEntry.arguments?.getString("sellerId")
            if (sellerId != null) {
                SellerProfileScreen(
                    sellerId = sellerId,
                    navController = navController
                )
            }
        }
        composable(
            route = Screen.CategoryProducts.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            if (categoryName != null) {
                CategoryProductScreen(
                    navController = navController,
                    categoryName = categoryName,
                    productViewModel = productViewModel
                )
            } else {
                navController.popBackStack() // Kembali jika nama kategori tidak ada
            }
        }
    }
}


@Composable
fun HomeScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel(),
    searchViewModel: SearchViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val realProducts by productViewModel.products.collectAsStateWithLifecycle()
    val mostViewedProduct by productViewModel.mostViewedProduct.collectAsStateWithLifecycle()
    val isLoading by productViewModel.isLoading.collectAsStateWithLifecycle()
    val searchUiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchBarFocused by remember { mutableStateOf(false) }
    var searchExecuted by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val displayedProducts = remember(realProducts, searchQuery, searchExecuted, selectedCategoryFilter) {
        val filteredBySearch = if (searchExecuted && searchQuery.isNotBlank()) {
            realProducts.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.brand.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        } else {
            realProducts
        }

        if (selectedCategoryFilter != null && selectedCategoryFilter != "All") {
            filteredBySearch.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        } else {
            filteredBySearch
        }
    }

    fun performSearch(query: String) {
        val trimmedQuery = query.trim()
        keyboardController?.hide()
        focusManager.clearFocus()

        searchViewModel.executeSearch(trimmedQuery, realProducts)

        searchQuery = trimmedQuery
        searchExecuted = trimmedQuery.isNotBlank()
        isSearchBarFocused = false
        selectedCategoryFilter = null
    }

    Scaffold(
        topBar = {
            Column (
                modifier = Modifier.statusBarsPadding()
            ) {
                // --- PERBAIKAN DI SINI ---
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
                    onCartClick = { navController.navigate(Screen.Cart.route) },
                    onNotificationClick = { navController.navigate("notifications") } // Tambahkan parameter yang hilang
                ) // <-- Tanda kurung tutup yang hilang ditambahkan di sini
                // --- AKHIR PERBAIKAN ---
            }
        },
        bottomBar = {
            if (!isSearchBarFocused && !searchExecuted) {
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
            else if (isSearchBarFocused) {
                SearchHistoryView(
                    history = searchUiState.searchHistory,
                    onHistoryClick = { historyTerm ->
                        searchQuery = historyTerm
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
                DefaultHomeScreenContent(
                    products = displayedProducts,
                    navController = navController,
                    mostViewedProduct = mostViewedProduct,
                    selectedCategoryFilter = selectedCategoryFilter,
                    onCategorySelected = { category ->
                        selectedCategoryFilter = category
                        searchQuery = ""
                        searchExecuted = false
                        searchViewModel.resetSearchState(realProducts)
                        navController.navigate(Screen.CategoryProducts.createRoute(category))
                    }
                )
            }
        }
    }
}

@Composable
fun DefaultHomeScreenContent(
    products: List<Product>,
    navController: NavController,
    mostViewedProduct: Product?,
    selectedCategoryFilter: String?,
    onCategorySelected: (String) -> Unit)
{
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        item {  FeaturedProductBanner(product = mostViewedProduct, navController = navController) }
        item {
            CategoryRow(
                selectedCategory = selectedCategoryFilter,
                onCategorySelected = onCategorySelected
            )
        }
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

@Composable
fun ProductCard(product: Product, navController: NavController) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    val isSoldOut = product.stock <= 0

    Column(
        modifier = Modifier.clickable(
            enabled = !isSoldOut,
            onClick = { navController.navigate("productDetail/${product.id}") }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            val displayImage = if (product.imageUrls.isNotEmpty()) {
                product.imageUrls.first()
            } else {
                product.imageUrl
            }
            AsyncImage(
                model = displayImage,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isSoldOut) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SOLD OUT",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val textColor = if (isSoldOut) Color.Gray.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface

        Text(product.brand, style = MaterialTheme.typography.labelSmall, color = Color.Gray, maxLines = 1)
        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            color = textColor
        )
        Text(
            text = formatCurrency.format(product.price),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        // --- TAMBAHKAN KODE INI UNTUK MENAMPILKAN STOK ---
        if (!isSoldOut) {
            Text(
                text = "Sisa stok: ${product.stock}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        // ---------------------------------------------
    }
}

@Composable
fun FeaturedProductBanner(product: Product?, navController: NavController) {
    val bannerImage = product?.imageUrls?.firstOrNull() ?: product?.imageUrl

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
            .clickable(
                enabled = product != null && product.stock > 0,
                onClick = {
                    navController.navigate("productDetail/${product?.id}")
                }
            )
    ) {
        AsyncImage(
            model = bannerImage,
            contentDescription = "Featured Product Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 100f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "Trending",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            if (product != null) {
                Text(
                    text = product.name,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryRow(selectedCategory: String?, onCategorySelected: (String) -> Unit) {
    // Gunakan daftar kategori dari AppConstants
    val categories = remember { AppConstants.PRODUCT_CATEGORIES }

    // Map kategori ke ikon placeholder. Anda perlu mengganti ini dengan ikon yang sebenarnya.
    val categoryIcons = remember {
        mapOf(
            "T-shirt" to R.drawable.t_shirt,
            "Trousers" to R.drawable.trousers,
            "Jacket" to R.drawable.jacket,
            "Footwear" to R.drawable.footwear,
            "Shirt" to R.drawable.shirt,
            "Dresses" to R.drawable.dress,
            "Sweaters" to R.drawable.sweater,
            "Shorts" to R.drawable.shorts,
            "Cardigans" to R.drawable.cardigan,
            "Hats" to R.drawable.hats,
            "Other" to R.drawable.others
        )
    }

    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Categories", fontWeight = FontWeight.Bold)
            // Anda bisa tambahkan logika navigasi ke layar semua kategori di sini jika diperlukan
            Text(">", modifier = Modifier.align(Alignment.CenterVertically).clickable {})
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            // Tampilkan setiap kategori sebagai CategoryGridItem
            items(categories) { category ->
                CategoryGridItem(
                    text = category,
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    iconResId = categoryIcons[category] ?: R.drawable.logo // Ambil ikon spesifik atau default logo
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryGridItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    iconResId: Int // Parameter untuk ID resource ikon
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(80.dp) // Lebar tetap untuk setiap item
            .padding(vertical = 4.dp), // Padding vertikal di sekitar item
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp) // Ukuran kotak latar belakang ikon
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray), // Warna latar belakang berdasarkan seleksi
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconResId), // Gunakan ID resource ikon yang diteruskan
                contentDescription = text,
                modifier = Modifier.size(36.dp), // Ukuran ikon itu sendiri
                tint = if (isSelected) Color.White else Color.DarkGray // Warna ikon berdasarkan seleksi
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall, // Font kecil untuk nama kategori
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black, // Warna teks berdasarkan seleksi
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProfileScreen(
    sellerId: String,
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel()
) {
    val sellerProfile by profileViewModel.specificUserProfile.collectAsStateWithLifecycle()
    val sellerProducts by productViewModel.userProducts.collectAsStateWithLifecycle()

    LaunchedEffect(sellerId) {
        profileViewModel.fetchUserProfileById(sellerId)
        productViewModel.fetchProductsByUserId(sellerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sellerProfile?.name ?: "Seller Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        if (sellerProfile != null) {
            Column(modifier = Modifier.padding(paddingValues)) {
                DefaultUserProfileContent(
                    navController = navController,
                    userProducts = sellerProducts,
                    productViewModel = productViewModel,
                    userProfile = sellerProfile,
                    isMyProfile = false
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductScreen(
    navController: NavController,
    categoryName: String,
    productViewModel: ProductViewModel = viewModel()
) {
    val allProducts by productViewModel.products.collectAsStateWithLifecycle()
    val isLoading by productViewModel.isLoading.collectAsStateWithLifecycle()

    // Filter produk berdasarkan categoryName
    val filteredProducts = remember(allProducts, categoryName) {
        if (categoryName == "All") { // Jika "All" dikirim, tampilkan semua produk
            allProducts
        } else {
            allProducts.filter { it.category.equals(categoryName, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = categoryName, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Tombol kembali
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading && filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No products found in this category.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    items(filteredProducts.chunked(2)) { productRow ->
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
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val fakeSearchViewModel = object : SearchViewModel() {
    }

    TugasAkhirProgmobTheme {
        val dummyNavController = rememberNavController()
        val fakeProductViewModel = ProductViewModel()
        HomeScreen(
            navController = dummyNavController,
            searchViewModel = fakeSearchViewModel,
            productViewModel = fakeProductViewModel
        )
    }
}