package com.example.tugasakhirprogmob

import androidx.compose.foundation.layout.*
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
import com.example.tugasakhirprogmob.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    navController: NavController,
    orderId: String, // Terima orderId, bukan nomor resi
    profileViewModel: ProfileViewModel = viewModel()
) {
    val order by profileViewModel.selectedOrder.collectAsStateWithLifecycle()

    // Ambil detail pesanan saat layar dibuka
    LaunchedEffect(orderId) {
        profileViewModel.fetchOrderById(orderId)
    }

    // Data riwayat pelacakan palsu
    val trackingHistory = listOf(
        "Paket telah diserahkan ke kurir EDGE Express" to "06 Jul 2025, 10:00",
        "Paket sedang dalam perjalanan ke pusat sortir Denpasar" to "06 Jul 2025, 15:30",
        "Paket telah tiba di pusat sortir Denpasar" to "06 Jul 2025, 21:45",
        "Paket sedang dalam proses pengiriman ke alamat Anda" to "07 Jul 2025, 08:15"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lacak Paket") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (order == null) {
                // Tampilkan loading jika data order belum siap
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Tampilkan detail jika data sudah ada
                Text(
                    "Nomor Resi: ${order!!.nomorResi ?: "N/A"}", // Tampilkan nomor resi dari objek order
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()

                // Tampilkan riwayat perjalanan
                trackingHistory.forEach { (status, date) ->
                    Column {
                        Text(status, fontWeight = FontWeight.SemiBold)
                        Text(date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                // Tampilkan status terakhir jika pesanan sudah tiba
                if (order!!.statusPengiriman == "Pesanan Tiba") {
                    val receiverName = order!!.receiverName ?: "penerima"
                    Column {
                        Text("Pesanan telah diterima oleh $receiverName", fontWeight = FontWeight.SemiBold, color = Color(0xFF4CAF50))
                        Text("07 Jul 2025, 11:00", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}