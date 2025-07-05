package com.example.tugasakhirprogmob

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tugasakhirprogmob.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaymentInstructionScreen(
    navController: NavController,
    orderId: String,
    method: String,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val paymentSuccess by profileViewModel.paymentSuccess.collectAsStateWithLifecycle()

    // --- AMBIL DATA ORDER YANG DIPILIH ---
    val selectedOrder by profileViewModel.selectedOrder.collectAsStateWithLifecycle()
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }

    // Efek untuk mengambil detail order saat layar pertama kali dibuka
    LaunchedEffect(orderId) {
        profileViewModel.fetchOrderById(orderId)
    }
    // -------------------------------------

    LaunchedEffect(paymentSuccess) {
        if (paymentSuccess) {
            isLoading = false
            Toast.makeText(context, "Pembayaran berhasil dikonfirmasi!", Toast.LENGTH_SHORT).show()
            profileViewModel.resetPaymentStatus()
            navController.navigate("order_success") {
                popUpTo(Screen.Home.route)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedOrder == null) {
            // Tampilkan loading jika data order belum siap
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Selesaikan Pembayaran",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Metode: ${method.replace("_", " ")}", textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))

                Text("Nomor Virtual Account:", style = MaterialTheme.typography.titleMedium)
                Text("8808 1234 5678 9012", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // --- GUNAKAN TOTAL HARGA ASLI ---
                Text(
                    "Total: ${formatCurrency.format(selectedOrder!!.totalPrice)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                // --------------------------------

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        isLoading = true
                        profileViewModel.processSimulatedPayment(orderId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Saya Sudah Bayar")
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Mengecek pembayaran...", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}