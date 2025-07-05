package com.example.tugasakhirprogmob

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.viewmodel.CartItem
import com.example.tugasakhirprogmob.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {
    val context = LocalContext.current
    val cartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    val subtotal by cartViewModel.subtotal.collectAsStateWithLifecycle()
    val orderPlaced by cartViewModel.orderPlacedSuccessfully.collectAsStateWithLifecycle()
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }

    // State untuk loading overlay
    var isLoading by remember { mutableStateOf(false) }

    // Efek ini akan berjalan ketika `orderPlaced` menjadi true
    LaunchedEffect(orderPlaced) {
        if (orderPlaced) {
            isLoading = false
            Toast.makeText(context, "Pesanan berhasil dibuat!", Toast.LENGTH_LONG).show()
            cartViewModel.resetOrderStatus()
            // Kembali ke halaman utama setelah berhasil
            navController.popBackStack(Screen.Home.route, inclusive = false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Order Summary", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Back")
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item { AddressSection() }
                    item { DeliverySection() }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item {
                        Text(
                            "Produk",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    items(cartItems) { item ->
                        CheckoutProductItem(item, formatCurrency)
                    }
                }

                // Order summary
                OrderSummary(
                    cartItems = cartItems,
                    subtotal = subtotal,
                    delivery = 0.0, // Placeholder
                    platformFee = 1000.0, // Placeholder
                    total = subtotal + 1000.0,
                    formatCurrency = formatCurrency
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Order button
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = {
                            isLoading = true
                            cartViewModel.placeOrder()
                        },
                        enabled = cartItems.isNotEmpty() && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Buat Pesanan (Simulasi)",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CheckoutProductItem(cartItem: CartItem, formatCurrency: NumberFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cartItem.imageUrl,
            contentDescription = cartItem.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .height(80.dp)
                .weight(1f)
        ) {
            Text(
                text = cartItem.name,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = formatCurrency.format(cartItem.price),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Text(
            text = "${cartItem.quantity}x",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun OrderSummary(
    cartItems: List<CartItem>,
    subtotal: Double,
    delivery: Double,
    platformFee: Double,
    total: Double,
    formatCurrency: NumberFormat
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Total summary",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        SummaryRow(label = "Subtotal (${cartItems.sumOf { it.quantity }})", value = formatCurrency.format(subtotal))
        SummaryRow(label = "Delivery", value = if (delivery == 0.0) "Free" else formatCurrency.format(delivery))
        SummaryRow(label = "Platform fee", value = formatCurrency.format(platformFee))
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SummaryRow(label = "Total", value = formatCurrency.format(total), isBold = true)
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold
        )
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
fun AddressSection() { /* Placeholder UI */
    Column(Modifier.padding(16.dp)) {
        Text("Alamat Pengiriman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Jalan Raya Kuta No. 123, Bali", style = MaterialTheme.typography.bodyMedium)
        Text("Ubah", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { })
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun DeliverySection() { /* Placeholder UI */
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Metode Pengiriman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Reguler (2-3 Hari)", style = MaterialTheme.typography.bodyMedium)
        Text("Ubah", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { })
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckoutScreenPreview() {
    TugasAkhirProgmobTheme {
        CheckoutScreen(navController = rememberNavController())
    }
}