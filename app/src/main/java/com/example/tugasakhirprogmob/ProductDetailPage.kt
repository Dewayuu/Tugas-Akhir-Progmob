package com.example.tugasakhirprogmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme

data class ProductDetail(
    val images: List<Int>,
    val name: String,
    val price: String,
    val postDate: String,
    val category: String,
    val brand: String,
    val seller: Seller,
    val description: String
)

data class Seller(
    val avatarRes: Int,
    val name: String,
    val location: String,
    val rating: Float,
    val reviewCount: Int,
    val joinDate: String
)

// Data sampel untuk digunakan di UI dan preview
val sampleSeller = Seller(
    avatarRes = R.drawable.pfp1,
    name = "Seller Name",
    location = "Location",
    rating = 4.5f,
    reviewCount = 3,
    joinDate = "2y,4mo"
)

val sampleProductDetail = ProductDetail(
    images = listOf(
        R.drawable.nike,
        R.drawable.pfp1,
        R.drawable.uniqlo,
        R.drawable.pfp1
    ),
    name = "Very Long Product Name",
    price = "$10.99",
    postDate = "2 Weeks Ago",
    category = "Categories",
    brand = "Brand",
    seller = sampleSeller,
    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
)

class ProductDetailPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TugasAkhirProgmobTheme {
                ProductDetailScreen(product = sampleProductDetail)
            }
        }
    }
}

@Composable
fun ProductDetailScreen(product: ProductDetail) {
    Scaffold(
        // Kita gunakan BottomNavBar yang sudah dibuat sebelumnya
//        bottomBar = { SearchBottomNavBar(selectedItem = 0) },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()) // Padding untuk bottom bar
        ) {
            // Bagian 1: Slider Gambar
            item { ImageSliderSection(images = product.images) }
            // Bagian 2: Judul & Harga Produk
            item { ProductTitleSection(name = product.name, price = product.price) }
            // Bagian 3: Metadata Produk
            item { ProductMetadataSection(product = product) }
            // Bagian 4: Info Penjual
            item { SellerInfoSection(seller = product.seller) }
            // Bagian 5: Deskripsi
            item { DescriptionSection(description = product.description) }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ImageSliderSection(images: List<Int>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Membuat gambar persegi
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Product Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Tombol Kembali (Back)
        IconButton(
            onClick = { /* TODO: Handle back navigation */ },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        // Indikator Pager (titik-titik)
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.Gray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
fun ProductTitleSection(name: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = price, style = MaterialTheme.typography.titleLarge, color = Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { /* TODO: Handle Add to Cart */ },
            modifier = Modifier
                .size(56.dp) // Ukuran tombolnya
                .background(Color.Black, RoundedCornerShape(16.dp))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.cart),
                contentDescription = "Add to Cart",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun ProductMetadataSection(product: ProductDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ikon ini akan tetap berukuran default (14.dp) karena kita tidak menentukan ukurannya
        InfoChip(
            icon = painterResource(id = R.drawable.clock),
            text = product.postDate,
            iconSize = 18.dp
        )

        InfoChip(
            icon = painterResource(id = R.drawable.footwear2),
            text = product.category,
            iconSize = 20.dp
        )

        Text("•", color = Color.Gray)
        Text(text = product.brand, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun SellerInfoSection(seller: Seller) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = seller.avatarRes),
                contentDescription = "Seller Avatar",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = seller.name, fontWeight = FontWeight.Bold)
                Text(text = seller.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = seller.rating.toString(), fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                }
                Text(text = "${seller.reviewCount} Reviews", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(text = seller.joinDate, fontWeight = FontWeight.Bold)
                Text(text = "Joined", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DescriptionSection(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp, // Memberi jarak antar baris agar mudah dibaca
            color = Color.Gray
        )
    }
}

@Composable
fun InfoChip(
    icon: Painter,
    text: String,
    iconSize: Dp = 14.dp
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductDetailScreenPreview() {
    TugasAkhirProgmobTheme {
        ProductDetailScreen(product = sampleProductDetail)
    }
}

