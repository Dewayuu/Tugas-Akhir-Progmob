package com.example.tugasakhirprogmob

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.viewmodel.CartViewModel
import com.example.tugasakhirprogmob.viewmodel.Product
import com.example.tugasakhirprogmob.viewmodel.ProductDetailViewModel
import com.example.tugasakhirprogmob.viewmodel.UserProfile
import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val product by viewModel.product.collectAsState()
    val sellerProfile by viewModel.sellerProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddToCartDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        viewModel.fetchProductById(productId)
        viewModel.incrementViewCount(productId)
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading && product == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (product != null) {
                // Menampilkan dialog kustom jika showAddToCartDialog true
                if (showAddToCartDialog) {
                    AddToCartDialog(
                        product = product!!,
                        onDismissRequest = { showAddToCartDialog = false },
                        onConfirm = { quantity ->
                            cartViewModel.addToCartWithQuantity(product!!, quantity)
                            showAddToCartDialog = false
                            Toast.makeText(context, "$quantity x ${product!!.name} ditambahkan", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { ImageSliderSection(product = product!!, onBackClick = onBackClick) }
                    item {
                        ProductTitleSection(
                            product = product!!,
                            onAddToCart = {
                                if (product!!.stock > 0) {
                                    showAddToCartDialog = true
                                }
                            }
                        )
                    }
                    item { ProductMetadataSection(product = product!!) }
                    item {
                        SellerInfoSection(
                            seller = sellerProfile,
                            navController = navController
                        )
                    }
                    item { DescriptionSection(description = product!!.description) }
                }
            } else {
                Text("Product not found.", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

// --- DIALOG BARU YANG LEBIH BAIK SECARA VISUAL ---
@Composable
fun AddToCartDialog(
    product: Product,
    onDismissRequest: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Bagian Info Produk
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = product.imageUrls.firstOrNull() ?: product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Column {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formatCurrency.format(product.price),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Bagian Stepper Kuantitas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Jumlah", style = MaterialTheme.typography.bodyLarge)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(horizontal = 4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = quantity > 1
                        ) {
                            Icon(painterResource(id = R.drawable.minus), "Kurangi")
                        }
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(30.dp),
                            textAlign = TextAlign.Center
                        )
                        IconButton(
                            onClick = { if (quantity < product.stock) quantity++ },
                            enabled = quantity < product.stock
                        ) {
                            Icon(painterResource(id = R.drawable.plus), "Tambah")
                        }
                    }
                }

                Text(
                    text = "Sisa stok: ${product.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Konfirmasi
                Button(
                    onClick = { onConfirm(quantity) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Tambahkan ke Keranjang")
                }
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


@Composable
fun ProductTitleSection(product: Product, onAddToCart: () -> Unit) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    val formattedPrice = remember(product.price) {
        try {
            formatCurrency.format(product.price)
        } catch (e: Exception) {
            "Rp ${product.price.toLong()}"
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = formattedPrice, style = MaterialTheme.typography.titleLarge, color = Color.Gray)

            // Show remaining stock or out of stock status
            Spacer(modifier = Modifier.height(4.dp))
            val stockText = if (product.stock > 0) "Sisa stok: ${product.stock}" else "Habis"
            val stockColor = if (product.stock > 0) Color.Gray else Color.Red
            Text(text = stockText, color = stockColor, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { onAddToCart() },
            enabled = product.stock > 0, // Disable button if stock is 0
            modifier = Modifier
                .size(56.dp)
                .background(
                    if (product.stock > 0) Color.Black else Color.Gray.copy(alpha = 0.5f), // Change button color
                    RoundedCornerShape(16.dp)
                )
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
fun SellerInfoSection(
    seller: UserProfile?,
    navController: NavController
) {
    if (seller == null) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp).height(80.dp)) {
            Box(modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(alpha = 0.5f)))
        }
        return
    }

    fun formatJoinDate(timestamp: Timestamp?): String {
        if (timestamp == null) return "N/A"
        val diff = Timestamp.now().seconds - timestamp.seconds
        val years = TimeUnit.SECONDS.toDays(diff) / 365
        val months = (TimeUnit.SECONDS.toDays(diff) % 365) / 30
        return when {
            years > 0 -> "${years}y, ${months}mo"
            months > 0 -> "${months}mo"
            else -> "<1mo"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate("sellerProfile/${seller.uid}")
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = seller.profilePictureUrl,
                contentDescription = "Seller Avatar",
                placeholder = painterResource(id = R.drawable.profile),
                error = painterResource(id = R.drawable.profile),
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = seller.name, fontWeight = FontWeight.Bold)
                Text(text = seller.address ?: "Location not set", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = formatJoinDate(seller.createdAt), fontWeight = FontWeight.Bold)
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