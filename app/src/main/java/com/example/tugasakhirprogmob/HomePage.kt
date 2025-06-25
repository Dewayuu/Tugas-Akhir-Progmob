package com.example.tugasakhirprogmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.ui.components.ProductCard
import com.example.tugasakhirprogmob.ui.components.SearchHistoryView
import com.example.tugasakhirprogmob.ui.components.ProductGrid
import com.example.tugasakhirprogmob.ui.components.SearchResultsHeader
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalFocusManager
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tugasakhirprogmob.ui.components.BottomNavBar
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TugasAkhirProgmobTheme {
                // Kita tidak lagi memanggil HomeScreen secara langsung di sini.
                // Sebaliknya, kita siapkan sistem navigasi.

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route // Layar awal adalah Home
                ) {
                    // Daftarkan HomeScreen sebagai tujuan untuk rute "home"
                    composable(Screen.Home.route) {
                        HomeScreen(navController = navController) // NavHost yang memanggil dan memberikan navController
                    }

                    // Daftarkan layar lain di sini nanti
                    composable(Screen.Search.route) {
                        // 1. Dapatkan instance ViewModel untuk layar ini
                        val viewModel: SearchViewModel = viewModel()

                        // 2. Ambil state dari ViewModel
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        // 3. Panggil SearchScreen dengan semua parameter yang dibutuhkan
                        SearchScreen(
                            navController = navController,
                            uiState = uiState,
                            onQueryChange = viewModel::onSearchQueryChanged,
                            onSearch = viewModel::executeSearch,
                            onSearchFocusChange = viewModel::onSearchFocused
                        )
                    }
                    composable(Screen.Add.route) {
                        // Panggil ProductCreateScreen di sini
                        ProductCreateScreen(
                            navController = navController, // 1. Teruskan navController utama
                            onBackClick = {
                                // 2. Tentukan apa yang terjadi saat tombol back diklik: kembali ke layar sebelumnya
                                navController.popBackStack()
                            }
                            // ViewModel akan otomatis dibuat oleh `viewModel()` di dalam ProductCreateScreen
                        )
                    }
                    // ... dan seterusnya untuk layar Add dan Profile
                }
            }
        }
    }
}

// --- Data Classes & Data Sampel ---
data class Product(val brand: String, val name: String, val price: String, val imageRes: Int)
data class ChipData(val label: String, val imageRes: Int)
data class CategoryItem(val label: String, val imageRes: Int)

val sampleProducts = listOf(
    Product("Nike", "Nike Air Zoom", "$120.99", R.drawable.nike),
    Product("Uniqlo", "Jacket Parka", "$75.50", R.drawable.uniqlo),
    Product("No Brand", "Basic T-Shirt", "$10.99", R.drawable.nike),
    Product("Adidas", "Adidas Ultraboost", "$150.00", R.drawable.nike),
    Product("Uniqlo", "Women's T-Shirt", "$15.99", R.drawable.uniqlo),
    Product("Nike", "Sport Jacket", "$89.99", R.drawable.nike)
)

// --- Composable Utama ---
@Composable
fun HomeScreen(navController: NavController) {
    // 1. MANAJEMEN STATE UNTUK FITUR PENCARIAN
    var searchQuery by remember { mutableStateOf("") }
    var isSearchBarFocused by remember { mutableStateOf(false) }
    var searchHistory by remember { mutableStateOf<List<String>>(emptyList()) }
    var displayedProducts by remember { mutableStateOf(sampleProducts) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchExecuted by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // Fungsi untuk menjalankan logika pencarian
//    fun performSearch(query: String) {
//        val trimmedQuery = query.trim()
//        if (trimmedQuery.isNotBlank()) {
//            // Filter produk berdasarkan nama atau brand
//            displayedProducts = sampleProducts.filter {
//                it.name.contains(trimmedQuery, ignoreCase = true) ||
//                        it.brand.contains(trimmedQuery, ignoreCase = true)
//            }
//            // Tambahkan ke riwayat jika belum ada
//            if (!searchHistory.contains(trimmedQuery)) {
//                searchHistory = (listOf(trimmedQuery) + searchHistory).take(10)
//            }
//        } else {
//            // Jika query kosong, tampilkan semua produk
//            displayedProducts = sampleProducts
//        }
//        isSearchBarFocused = false
//        keyboardController?.hide()
//    }
    fun performSearch(query: String) {
        val trimmedQuery = query.trim()
//        searchQuery = trimmedQuery

        keyboardController?.hide()
        focusManager.clearFocus()

        if (trimmedQuery.isNotBlank()) {
            // Update history jika perlu
            if (!searchHistory.contains(trimmedQuery)) {
                val newHistory = (listOf(trimmedQuery) + searchHistory).take(10)
                searchHistory = newHistory
            }
            // Update produk yang ditampilkan
            displayedProducts = sampleProducts.filter {
                it.name.contains(trimmedQuery, ignoreCase = true) ||
                        it.brand.contains(trimmedQuery, ignoreCase = true)
            }
            // Update state untuk UI
            searchQuery = trimmedQuery
            searchExecuted = true
            isSearchBarFocused = false

        } else {
            // Jika query kosong, reset semuanya
            searchQuery = ""
            searchExecuted = false
            isSearchBarFocused = false
            displayedProducts = sampleProducts
        }
    }

//        if (trimmedQuery.isNotBlank()) {
//            searchExecuted = true
//            displayedProducts = sampleProducts.filter {
//                it.name.contains(trimmedQuery, ignoreCase = true) ||
//                        it.brand.contains(trimmedQuery, ignoreCase = true)
//            }
//
//            // --- LOGIKA UNTUK MEMPERBARUI RIWAYAT PENCARIAN ---
//            // 1. Cek agar tidak menambahkan kata yang sama berulang kali
//            if (!searchHistory.contains(trimmedQuery)) {
//                // 2. Tambahkan query baru ke PALING ATAS daftar
//                val newHistory = listOf(trimmedQuery) + searchHistory
//                // 3. Batasi jumlah riwayat (misalnya 10 terakhir) dan perbarui state
//                searchHistory = newHistory.take(10)
//            }
//            // ---------------------------------------------------
//
//        } else {
//            searchExecuted = false
//            displayedProducts = sampleProducts
//        }
//        isSearchBarFocused = false
//        keyboardController?.hide()
//        focusManager.clearFocus()
//    }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        // TopBar di-upgrade untuk menerima state dan event
//        TopBar(
//            query = searchQuery,
//            onQueryChange = { searchQuery = it },
//            onSearch = { performSearch(it) },
//            onFocusChange = { isFocused -> isSearchBarFocused = isFocused }
//        )
//
//        // 2. TAMPILAN KONDISIONAL: Riwayat atau Konten Utama
//        if (isSearchBarFocused) {
//            SearchHistoryView(
//                history = searchHistory,
//                onHistoryClick = { historyTerm: String -> // Menambahkan tipe : String adalah praktik yang baik
//                    searchQuery = historyTerm
//                    performSearch(historyTerm)
//                }
//            )
//        } else if (searchExecuted) {
//            // TAMPILAN SETELAH PENCARIAN DIJALANKAN
//            Column {
//                FilterChips()
//
//                // <<<--- DIPAKAI DI SINI
//                SearchResultsHeader(query = searchQuery)
//
//                ProductGrid(products = displayedProducts)
//            }
//
//        } else {
//            // TAMPILAN DEFAULT HOMEPAGE (Di sini memang tidak ada SearchResultsHeader)
//            LazyColumn(modifier = Modifier.weight(1f)) {
//                item { FilterChips() }
//                item { TopSellingBanner() }
//                item { CategoryRow() }
//                item {
//                    ProductSection(title = "Newest", products = displayedProducts)
//                }
//            }
//        }
//
//        BottomNavBar()
//    }
//}
    Scaffold(
        topBar = {
            TopBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    if(it.isBlank()){
                        searchExecuted = false
                        displayedProducts = sampleProducts
                    }
                },
                onSearch = { performSearch(it) },
                onFocusChange = { isFocused -> isSearchBarFocused = isFocused },
                onCartClick = {
                    // Membuat Intent untuk membuka ViewCartActivity
                    val intent = Intent(context, ViewCart::class.java)
                    // Menjalankan Intent untuk memulai Activity baru
                    context.startActivity(intent)
                }
            )
        },
        bottomBar = {
            if (!isSearchBarFocused && !searchExecuted) {
                BottomNavBar(navController = navController)
            }
        }
    ) { paddingValues ->
        // Konten utama diletakkan di sini, dengan padding dari Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Terapkan padding dari Scaffold
        ) {
            // --- LOGIKA TAMPILAN KONTEN YANG SAMA SEPERTI SEBELUMNYA ---
            if (isSearchBarFocused) {
                SearchHistoryView(
                    history = searchHistory,
                    onHistoryClick = { historyTerm ->
                        performSearch(historyTerm)
                    }
                )
            } else if (searchExecuted) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header tetap ditampilkan agar pengguna tahu apa yang mereka cari
                    SearchResultsHeader(query = searchQuery)

                    // Cek apakah daftar produknya kosong
                    if (displayedProducts.isEmpty()) {
                        // JIKA KOSONG: Tampilkan pesan "Product not found"
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.search), // Ganti dengan ikon yang lebih sesuai jika ada
                                    contentDescription = "Not Found",
                                    modifier = Modifier.size(80.dp)
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
                        // JIKA ADA ISINYA: Tampilkan grid produk seperti biasa
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            // Grid produk disimulasikan di sini
                            items(displayedProducts.chunked(2)) { productRow ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    productRow.forEach { product ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            ProductCard(product = product)
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
            } else {
                // Tampilan default homepage
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Header dan banner tetap sebagai item biasa
                    item { FilterChips() }
                    item { TopSellingBanner() }
                    item { CategoryRow() }

                    // Header untuk produk "Newest" menjadi item sendiri
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Newest", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(">")
                        }
                    }

                    // Grid produk "Newest" disimulasikan di sini, sama seperti hasil pencarian
                    items(sampleProducts.chunked(2)) { productRow ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            productRow.forEach { product ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCard(product = product)
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
}

// --- Composable yang Diperbarui dan Ditambahkan ---

@Composable
fun TopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search Edge") },
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .onFocusChanged { focusState -> onFocusChange(focusState.isFocused) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Gray
            ),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(query) })
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { onCartClick() }) {
            Icon(painter = painterResource(id = R.drawable.cart),
                contentDescription = "Cart",
                modifier = Modifier.size(28.dp))
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
//                    imageVector = Icons.Default.History,
//                    contentDescription = "History Icon",
//                    tint = Color.Gray
//                )
//                Spacer(modifier = Modifier.width(16.dp))
//                Text(text = term, style = MaterialTheme.typography.bodyLarge)
//            }
//        }
//    }
//}

//@Composable
//fun ProductSection(title: String, products: List<Product>) {
//    Column(modifier = Modifier.padding(16.dp)) {
//        Row(
//            Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
//            if (title == "Newest") {
//                Text(">", modifier = Modifier.align(Alignment.CenterVertically))
//            }
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            modifier = Modifier.heightIn(max = 1000.dp), // Izinkan Grid untuk expand
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            items(products) { product ->
//                ProductCard(product = product) // Menggunakan ProductCard baru
//            }
//        }
//        ProductGrid(products = products)
//    }
//}

//@Composable
//fun ProductCard(product: Product) {
//    Column(modifier = Modifier.clickable { /* TODO: Navigasi ke detail produk */ }) {
//        Image(
//            painter = painterResource(product.imageRes),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(1f) // Membuat gambar menjadi persegi
//                .clip(RoundedCornerShape(12.dp))
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(product.brand, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
//        Text(product.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
//        Text(product.price, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
//    }
//}


@Composable
fun FilterChips() {
    val chips = listOf(
        ChipData("Filters", R.drawable.mage_filter),
        ChipData("Jacket", R.drawable.jacket),
        ChipData("Women's Clothing", R.drawable.t_shirt)
    )

    LazyRow(modifier = Modifier.padding(start = 16.dp)) {
        items(chips) { chip ->
            AssistChip(
                onClick = {},
                label = { Text(chip.label) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = chip.imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun TopSellingBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(16.dp)
            .background(Color.LightGray, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Top Selling", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CategoryRow() {
    val categories = listOf(
        CategoryItem("T-Shirt", R.drawable.t_shirt),
        CategoryItem("Trousers", R.drawable.trousers),
        CategoryItem("Footwear", R.drawable.footwear),
        CategoryItem("Jacket", R.drawable.jacket),
        CategoryItem("T-Shirt", R.drawable.t_shirt),
        CategoryItem("Trousers", R.drawable.trousers),
    )

    Column(Modifier.padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Categories", fontWeight = FontWeight.Bold)
            Text(">", modifier = Modifier.align(Alignment.CenterVertically))
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(categories) { category ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .width(75.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = category.imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp) // Atur sesuai ukuran gambarmu
                        )
                    }
                    Text(category.label)
                }
            }
        }
    }
}

//@Composable
//fun NewestProductsSection() {
//    Column(modifier = Modifier.padding(16.dp)) {
//        Row(
//            Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text("Newest", fontWeight = FontWeight.Bold)
//            Text(">")
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.height(300.dp)) {
//            items(sampleProducts) { product ->
//                Column(
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .fillMaxWidth()
//                ) {
//                    Image(
//                        painter = painterResource(product.imageRes),
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .height(120.dp)
//                            .fillMaxWidth()
//                            .shadow(1.dp, RoundedCornerShape(8.dp))
//                            .clip(RoundedCornerShape(8.dp))
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(product.brand, style = MaterialTheme.typography.labelSmall)
//                    Text(product.name)
//                    Text(product.price, fontWeight = FontWeight.Bold)
//                }
//            }
//        }
//    }
//}

@Composable
fun BottomNavBar() {
    val selectedIndex = remember { mutableStateOf(0) }

    NavigationBar {
        NavigationBarItem(
            selected = selectedIndex.value == 0,
            onClick = { selectedIndex.value = 0 },
            icon = {
                Image(
                    painter = painterResource(
                        id = if (selectedIndex.value == 0) R.drawable.home_filled
                        else R.drawable.home_outline
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedIndex.value == 1,
            onClick = { selectedIndex.value = 1 },
            icon = {
                Image(
                    painter = painterResource(
                        id = if (selectedIndex.value == 1) R.drawable.search2
                        else R.drawable.search2
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedIndex.value == 2,
            onClick = { selectedIndex.value = 2 },
            icon = {
                Image(
                    painter = painterResource(
                        id = if (selectedIndex.value == 2) R.drawable.add
                        else R.drawable.add
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedIndex.value == 3,
            onClick = { selectedIndex.value = 3 },
            icon = {
                Image(
                    painter = painterResource(
                        id = if (selectedIndex.value == 3) R.drawable.profile
                        else R.drawable.profile
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TugasAkhirProgmobTheme {
        // Buat NavController bohongan untuk preview
        val dummyNavController = rememberNavController()
        // Berikan NavController bohongan tersebut saat memanggil HomeScreen
        HomeScreen(navController = dummyNavController)
    }
}
