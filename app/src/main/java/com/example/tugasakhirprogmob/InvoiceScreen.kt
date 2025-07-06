package com.example.tugasakhirprogmob

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun InvoiceScreen(
    navController: NavController,
    orderId: String,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val order by profileViewModel.selectedOrder.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        profileViewModel.fetchOrderById(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Back")
                    }
                }
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("INVOICE #${order!!.orderId.uppercase()}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    InvoiceInfo(order!!)
                    Spacer(modifier = Modifier.height(16.dp))
                    InvoiceItems(order!!)
                }
            }
        }
    }
}

@Composable
private fun InvoiceInfo(order: Order) {
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Diterbitkan untuk:", color = Color.Gray)
        Text(order.userId, fontWeight = FontWeight.SemiBold) // Ganti dengan nama user nanti
        Text(order.alamatPengiriman)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tanggal: ${order.createdAt?.let { dateFormat.format(it) } ?: "-"}", color = Color.Gray)
    }
}

@Composable
private fun InvoiceItems(order: Order) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    Column(
        modifier = Modifier
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp)
    ) {
        // Header
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("PRODUK", modifier = Modifier.weight(1f), color = Color.Gray, fontWeight = FontWeight.Bold)
            Text("TOTAL", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Items
        order.items.forEach { item ->
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.SemiBold)
                    Text("${item.quantity} x ${formatCurrency.format(item.price)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Text(formatCurrency.format(item.price * item.quantity))
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Total
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Subtotal", modifier = Modifier.weight(1f), color = Color.Gray)
            Text(formatCurrency.format(order.totalPrice))
        }
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Pengiriman", modifier = Modifier.weight(1f), color = Color.Gray)
            Text("Gratis")
        }
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("TOTAL", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(formatCurrency.format(order.totalPrice), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
    }
}