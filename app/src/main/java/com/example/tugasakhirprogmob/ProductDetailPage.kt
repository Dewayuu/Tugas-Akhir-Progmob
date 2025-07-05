package com.example.tugasakhirprogmob

// --- TAMBAHKAN IMPORT INI ---
import android.widget.Toast
// -----------------------------
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
// --- TAMBAHKAN IMPORT INI ---
import androidx.compose.ui.platform.LocalContext
// -----------------------------
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
// --- TAMBAHKAN IMPORT INI ---
import com.example.tugasakhirprogmob.viewmodel.CartViewModel
// -----------------------------
import com.example.tugasakhirprogmob.viewmodel.Product
import com.example.tugasakhirprogmob.viewmodel.ProductDetailViewModel
import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

// --- UBAH BAGIAN INI ---
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel() // Tambahkan parameter ini
) {
    val context = LocalContext.current // Tambahkan ini untuk Toast
    // ----------------------
    // Ambil data dari ViewModel
    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Panggil fetchProductById saat layar pertama kali dibuat
    LaunchedEffect(productId) {
        viewModel.fetchProductById(productId)
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (product != null) {
                // Jika produk berhasil diambil, tampilkan kontennya
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { ImageSliderSection(product = product!!, onBackClick = onBackClick) }
                    // --- UBAH BAGIAN INI ---
                    item {
                        ProductTitleSection(
                            name = product!!.name,
                            price = product!!.price,
                            onAddToCart = {
                                cartViewModel.addToCart(product!!)
                                // Beri feedback ke pengguna
                                Toast.makeText(context, "${product!!.name} ditambahkan", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    // -----------------------
                    item { ProductMetadataSection(product = product!!) }
                    item { SellerInfoSection(sellerName = product!!.sellerName) }
                    item { DescriptionSection(description = product!!.description) }
                }
            } else {
                // Tampilkan pesan jika produk tidak ditemukan
                Text("Product not found.", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageSliderSection(product: Product, onBackClick: () -> Unit) {
    val images = if (product.imageUrls.isNotEmpty()) {
        product.imageUrls
    } else {
        listOfNotNull(product.imageUrl)
    }

    if (images.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("No Image Available")
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            AsyncImage(
                model = images.getOrNull(page),
                contentDescription = "Product Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().background(Color.LightGray)
            )
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        if (images.size > 1) {
            Row(
                Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.Gray
                    Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(8.dp))
                }
            }
        }
    }
}

// --- UBAH BAGIAN INI ---
@Composable
fun ProductTitleSection(name: String, price: Double, onAddToCart: () -> Unit) { // Tambahkan parameter onAddToCart
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    val formattedPrice = remember(price) {
        try {
            formatCurrency.format(price)
        } catch (e: Exception) {
            "Rp ${price.toLong()}"
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = formattedPrice, style = MaterialTheme.typography.titleLarge, color = Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { onAddToCart() }, // Panggil lambda di sini
            modifier = Modifier
                .size(56.dp)
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
// -----------------------

// ... Sisa kode dari SellerInfoSection, DescriptionSection, dan InfoChip tidak perlu diubah, biarkan seperti aslinya.
// Di bawah ini adalah sisa kode yang tidak berubah.

@Composable
fun ProductMetadataSection(product: Product) {
    fun formatPostDate(timestamp: Timestamp?): String {
        if (timestamp == null) return "N/A"
        val now = Timestamp.now().seconds
        val diff = now - timestamp.seconds
        val days = TimeUnit.SECONDS.toDays(diff)
        return when {
            days < 1 -> "Today"
            days < 2 -> "Yesterday"
            days < 30 -> "$days days ago"
            else -> "${days / 30} months ago"
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfoChip(icon = painterResource(id = R.drawable.clock), text = formatPostDate(product.postedAt), iconSize = 18.dp)
        InfoChip(icon = painterResource(id = R.drawable.footwear2), text = product.category, iconSize = 20.dp)
        Text("•", color = Color.Gray)
        Text(text = product.brand, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun SellerInfoSection(sellerName: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.pfp1), contentDescription = "Seller Avatar", modifier = Modifier.size(50.dp).clip(CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = sellerName, fontWeight = FontWeight.Bold)
                Text(text = "Location", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
            lineHeight = 22.sp,
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