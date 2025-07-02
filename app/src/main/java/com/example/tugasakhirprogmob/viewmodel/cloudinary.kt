package com.example.tugasakhirprogmob.viewmodel

import android.content.Context
import android.util.Log
import com.cloudinary.android.MediaManager
import com.example.tugasakhirprogmob.BuildConfig

object CloudinaryManager {
    private var isInitialized = false

    fun init(context: Context) {
        // Hanya inisialisasi sekali untuk mencegah error
        if (isInitialized) return

        try {
            val config = mapOf(
                "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
                "api_key" to BuildConfig.CLOUDINARY_API_KEY,
                "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
            )
            MediaManager.init(context, config)
            isInitialized = true
            Log.d("CloudinaryManager", "Cloudinary SDK initialized successfully.")
        } catch (e: Exception) {
            Log.e("CloudinaryManager", "Cloudinary initialization failed. Check credentials.", e)
        }
    }
}
