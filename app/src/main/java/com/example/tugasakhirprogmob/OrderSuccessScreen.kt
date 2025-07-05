package com.example.tugasakhirprogmob

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme

@Composable
fun OrderSuccessScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.checkmark), // Anda perlu menambahkan ikon checkmark ke drawable
                contentDescription = "Success",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Pesanan Berhasil Dibuat!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Terima kasih telah berbelanja. Anda dapat melihat detail pesanan Anda di halaman profil.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = {
                    // Kembali ke halaman utama
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    "Kembali ke Beranda",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Catatan: Anda perlu menambahkan ikon 'checkmark.xml' ke folder res/drawable.
// Anda bisa mencari ikon checkmark gratis dari situs seperti Flaticon atau membuatnya sendiri.

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderSuccessScreenPreview() {
    TugasAkhirProgmobTheme {
        OrderSuccessScreen(navController = rememberNavController())
    }
}