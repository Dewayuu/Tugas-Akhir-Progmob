package com.example.tugasakhirprogmob.ui.components

import java.text.NumberFormat
import java.util.Locale
import androidx.compose.runtime.remember
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tugasakhirprogmob.viewmodel.Product
import com.example.tugasakhirprogmob.R
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tugasakhirprogmob.Screen
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage



@Composable
fun BottomNavBar(navController: NavController) {
    // Daftar item navigasi kita dari class Screen
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Add,
        Screen.Profile
    )

    val icons = mapOf(
        Screen.Home to Pair(R.drawable.home_outline, R.drawable.home_filled),
        Screen.Search to Pair(R.drawable.search2, R.drawable.search_filled),
        Screen.Add to Pair(R.drawable.add, R.drawable.add_filled),
        Screen.Profile to Pair(R.drawable.profile, R.drawable.profile_filled)
    )
    // ----------------------

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val iconPair = icons[screen] ?: return@forEach // Ambil pasangan ikon, lewati jika tidak ada

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Image(
                        painter = painterResource(
                            // Logika untuk memilih ikon filled atau outline
                            id = if (isSelected) iconPair.second else iconPair.first
                        ),
                        contentDescription = screen.route,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
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
fun ProductCard(product: Product) {
    val formatCurrency = remember {
        NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth(), // Dihapus .padding(8.dp) agar konsisten jika dipanggil dari grid
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = product.imageUrl, // Gunakan properti imageUrl yang bertipe String
                contentDescription = product.name,
                contentScale = ContentScale.Crop, // Agar gambar pas dengan ukuran
                modifier = Modifier
                    .size(100.dp) // sesuaikan modifier dengan kebutuhan Anda
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray) // Placeholder saat gambar sedang dimuat
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.brand,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = formatCurrency.format(product.price),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 3. KOMPONEN BARU YANG DIPINDAHKAN
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

// 4. KOMPONEN BARU YANG DIPINDAHKAN
@Composable
fun ProductGrid(products: List<Product>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(horizontal = 8.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductCard(product = product)
        }
    }
}