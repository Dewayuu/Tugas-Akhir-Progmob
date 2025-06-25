package com.example.tugasakhirprogmob

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.ui.components.BottomNavBar
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.viewmodel.ProductViewModel
import java.text.NumberFormat
import java.util.Locale

// Wrapper utama aplikasi dengan Navigasi.
// Ini bisa berada di MainActivity.kt atau di sini.
// Kita tidak mengubah bagian ini, sesuai permintaan Anda.
@Composable
fun MainApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        // PERBAIKAN: Ubah nama rute di sini agar cocok dengan yang dipanggil
        composable("add") {
            ProductCreateScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }
        // ... rute lain seperti "profile", "chat" bisa ditambahkan di sini
    }
}


@Composable
fun HomeScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel()
) {
    val context = LocalContext.current

    // Mengamati state dari ViewModel
    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()

    // Panggil fetchProducts() saat Composable pertama kali dibuat
    // Ini akan memicu pengambilan data dari Firestore
    LaunchedEffect(Unit) {
        productViewModel.fetchProducts()
    }

    Scaffold(
        topBar = {
            TopBar(
                onCartClick = {
                    val intent = Intent(context, ViewCart::class.java)
                    context.startActivity(intent)
                }
            )
        },
        bottomBar = {
            // BottomNavBar yang sudah ada digunakan di sini
            // Logikanya tidak diubah
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tampilkan loading indicator hanya saat data sedang diambil pertama kali
            if (isLoading && products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Tampilkan konten utama jika tidak loading atau data sudah ada
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // Item statis seperti banner dan filter tetap ada
                    item { FilterChips() }
                    item { TopSellingBanner() }
                    item { CategoryRow() }
                    item {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Newest", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(">", Modifier.clickable { /* TODO: Navigasi ke halaman 'semua produk' */ })
                        }
                    }

                    // INI BAGIAN UTAMA: Grid untuk menampilkan produk dari Firestore
                    // `products.chunked(2)` membagi list produk menjadi baris-baris berisi 2 item
                    items(products.chunked(2)) { productRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            productRow.forEach { product ->
                                // Setiap produk ditampilkan dalam ProductCard
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCard(product = product)
                                }
                            }
                            // Tambahkan Spacer jika jumlah item ganjil agar rata kiri
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

// ProductCard ini sekarang menerima data Product dari ViewModel
@Composable
fun ProductCard(product: com.example.tugasakhirprogmob.viewmodel.Product) {
    // Helper untuk memformat harga ke dalam Rupiah
    val formatCurrency = remember {
        NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    }

    Column(modifier = Modifier.clickable { /* TODO: Navigasi ke detail produk dengan ID product.id */ }) {
        // Menggunakan AsyncImage dari pustaka Coil untuk memuat gambar dari URL
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Membuat gambar menjadi persegi
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray) // Latar belakang placeholder saat gambar loading
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(product.brand, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(product.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Text(formatCurrency.format(product.price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun TopBar(onCartClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Edge", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onCartClick) {
            Icon(
                painter = painterResource(id = R.drawable.cart),
                contentDescription = "Cart",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}


// --- Composable lainnya (tetap sama) ---

@Composable
fun FilterChips() {
    val chips = listOf("Filters", "Jacket", "Women's Clothing")
    LazyRow(modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)) {
        items(chips) { chip ->
            AssistChip(
                onClick = {},
                label = { Text(chip) },
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


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TugasAkhirProgmobTheme {
        val dummyNavController = rememberNavController()
        HomeScreen(navController = dummyNavController)
    }
}
