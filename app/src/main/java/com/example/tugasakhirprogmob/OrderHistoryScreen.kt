package com.example.tugasakhirprogmob

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.viewmodel.Order
import com.example.tugasakhirprogmob.viewmodel.ProfileViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val orders by profileViewModel.orders.collectAsStateWithLifecycle()
    val isLoading by profileViewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pesanan", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Back")
                    }
                },
                actions = { Spacer(Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            orders.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text("Anda belum memiliki riwayat pesanan.", color = Color.Gray)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(orders, key = { it.orderId }) { order ->
                        Box(modifier = Modifier.clickable { navController.navigate("order_detail/${order.orderId}") }) {
                            OrderItemCard(order = order) {
                                // onPayClick sekarang mengarah ke alur pembayaran Ide 1
                                navController.navigate("payment_method/${order.orderId}")
                            }
                        }
                    }

                    // --------------------------
                }
            }
        }
    }
}



@Composable
fun OrderItemCard(order: Order, onPayClick: () -> Unit) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    val dateFormat = remember { SimpleDateFormat("dd MMMM yy, HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Baris ID Pesanan dan Tanggal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pesanan #${order.orderId.take(6).uppercase()}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.createdAt?.let { dateFormat.format(it) } ?: "...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            HorizontalDivider()

            // Daftar Item Produk
            order.items.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("${item.quantity}x", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                    Text(formatCurrency.format(item.price * item.quantity), style = MaterialTheme.typography.bodyMedium)
                }
            }
            HorizontalDivider()

            // Baris Total Pesanan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Pesanan", fontWeight = FontWeight.SemiBold)
                Text(
                    formatCurrency.format(order.totalPrice),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Selalu tampilkan status, apa pun kondisinya
                Text(
                    text = "Status: ${order.status}",
                    color = if (order.status == "Pending") Color.Red else Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )

                // Tampilkan tombol "Bayar" HANYA jika statusnya "Pending"
                if (order.status == "Pending") {
                    Button(
                        onClick = onPayClick, // Panggil lambda yang diteruskan
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Bayar Sekarang")
                    }
                }
            }
            // -----------------------------
        }
    }
}

// -----------------------------

@Preview(showBackground = true)
@Composable
private fun OrderHistoryScreenPreview() {
    TugasAkhirProgmobTheme {
        OrderHistoryScreen(rememberNavController())
    }
}