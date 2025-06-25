package com.example.tugasakhirprogmob

import android.app.Application
import com.google.firebase.FirebaseApp

// NAMA KELAS HARUS MyApplication, SESUAI DENGAN YANG DI MANIFEST
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inisialisasi Firebase saat aplikasi pertama kali dibuat
        FirebaseApp.initializeApp(this)
    }
}
