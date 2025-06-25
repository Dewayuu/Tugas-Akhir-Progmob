package com.example.tugasakhirprogmob

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pemeriksaan keamanan: Jika pengguna secara tidak sengaja sampai ke activity ini
        // tanpa login, arahkan mereka kembali ke halaman Login.
        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, Login::class.java))
            finish() // Tutup MainActivity agar tidak bisa kembali ke sini dengan tombol back
            return   // Hentikan eksekusi lebih lanjut dari fungsi onCreate
        }

        setContent {
            TugasAkhirProgmobTheme {
                // MainApp adalah Composable yang berisi NavHost dan Bottom Navigation Bar.
                // Ini adalah titik masuk untuk bagian aplikasi yang memerlukan autentikasi.
                // Pastikan fungsi @Composable MainApp() sudah ada di dalam proyek Anda
                // (misalnya di dalam file HomePage.kt seperti yang kita buat sebelumnya).
                MainApp()
            }
        }
    }
}
