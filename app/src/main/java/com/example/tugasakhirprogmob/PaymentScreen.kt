package com.example.tugasakhirprogmob

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tugasakhirprogmob.viewmodel.ProfileViewModel

@Composable
fun PaymentScreen(
    navController: NavController,
    orderId: String,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val paymentSuccess by profileViewModel.paymentSuccess.collectAsStateWithLifecycle()

    LaunchedEffect(paymentSuccess) {
        if (paymentSuccess) {
            Toast.makeText(context, "Pembayaran berhasil dikonfirmasi!", Toast.LENGTH_SHORT).show()
            profileViewModel.resetPaymentStatus()
            navController.popBackStack() // Kembali ke halaman riwayat pesanan
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Konfirmasi Pembayaran", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Ini adalah halaman simulasi. Tekan tombol di bawah untuk mengonfirmasi pembayaran Anda untuk pesanan #${orderId.take(6).uppercase()}.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { profileViewModel.confirmPayment(orderId) },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Konfirmasi Pembayaran")
        }
    }
}