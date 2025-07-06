package com.example.tugasakhirprogmob

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tugasakhirprogmob.viewmodel.Order
import com.example.tugasakhirprogmob.viewmodel.ProfileViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val order by profileViewModel.selectedOrder.collectAsStateWithLifecycle()
    var showMenu by remember { mutableStateOf(false) }

    // Ambil detail pesanan saat layar pertama kali dibuka
    LaunchedEffect(orderId) {
        profileViewModel.fetchOrderById(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Back")
                    }
                },
                // --- MENU OPSI TERSEMBUNYI ---
                actions = {
                    // Tampilkan menu hanya jika pesanan belum selesai
                    if (order != null && order!!.statusPengiriman != "Pesanan Tiba") {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Opsi")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Simulasikan Update Penjual") },
                                    onClick = {
                                        profileViewModel.simulateNextShipmentStep(order!!)
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
                // -----------------------------
            )
        }
    ) { innerPadding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { OrderInfoSection(order!!) }
                item { ProductListSection(order!!) }
                item { ShippingInfoSection(order!!) }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate("invoice/${order!!.orderId}") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Lihat Invoice")
                        }
                        Button(
                            onClick = { navController.navigate("tracking/${order!!.orderId}") }, // <-- UBAH INI
                            modifier = Modifier.weight(1f),
                            enabled = order!!.nomorResi != null
                        ) {
                            Text("Lacak Paket")
                        }
                    }
                }
            }
        }
    }
}

// Composable lainnya untuk setiap bagian
@Composable
private fun OrderInfoSection(order: Order) {
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Informasi Pesanan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("No. Pesanan", color = Color.Gray)
                Text(order.orderId.take(8).uppercase(), fontWeight = FontWeight.SemiBold)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tanggal", color = Color.Gray)
                Text(order.createdAt?.let { dateFormat.format(it) } ?: "-", fontWeight = FontWeight.SemiBold)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Status", color = Color.Gray)
                Text(order.status, fontWeight = FontWeight.SemiBold, color = if (order.status == "Lunas") Color(0xFF4CAF50) else Color.Red)
            }
        }
    }
}

@Composable
private fun ProductListSection(order: Order) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Produk yang Dipesan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        order.items.forEach { item ->
            Row {
                Text("${item.quantity}x", color = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Text(item.name, modifier = Modifier.weight(1f))
                Text(formatCurrency.format(item.price * item.quantity))
            }
        }
    }
}

@Composable
private fun ShippingInfoSection(order: Order) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Informasi Pengiriman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Alamat:", fontWeight = FontWeight.SemiBold)
        Text(order.alamatPengiriman)
        Spacer(modifier = Modifier.height(8.dp))
        Text("No. Resi:", fontWeight = FontWeight.SemiBold)
        Text(order.nomorResi ?: "Belum Tersedia")
        Spacer(modifier = Modifier.height(16.dp))
        ShippingStatusTimeline(currentStatus = order.statusPengiriman)
    }
}

@Composable
private fun ShippingStatusTimeline(currentStatus: String) {
    val statuses = listOf(
        "Menunggu Pembayaran",
        "Pesanan Diproses",
        "Pesanan Dikemas",
        "Pesanan Dikirim",
        "Pesanan Tiba"
    )
    val currentStatusIndex = statuses.indexOf(currentStatus).coerceAtLeast(0)

    Column {
        statuses.forEachIndexed { index, status ->
            StatusItem(
                title = status,
                isDone = index <= currentStatusIndex,
                isActive = index == currentStatusIndex
            )
            if (index < statuses.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(start = 11.dp)
                        .width(2.dp)
                        .height(32.dp)
                        .background(if (index < currentStatusIndex) Color(0xFF4CAF50) else Color.LightGray)
                )
            }
        }
    }
}

@Composable
private fun StatusItem(title: String, isDone: Boolean, isActive: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (isDone) Color(0xFF4CAF50) else Color.LightGray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    painter = painterResource(id = R.drawable.checkmark),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal)
    }
}