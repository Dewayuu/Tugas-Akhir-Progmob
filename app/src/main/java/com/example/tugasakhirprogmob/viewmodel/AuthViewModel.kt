package com.example.tugasakhirprogmob.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Data class untuk merepresentasikan data pengguna di Firestore
data class User(
    val uid: String,
    val name: String,
    val email: String,
    val createdAt: FieldValue
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    // State untuk mengelola status loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State untuk menandakan keberhasilan operasi (login/register)
    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess

    // State untuk menampung pesan error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Fungsi untuk Registrasi
    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 1. Buat pengguna di Firebase Authentication
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // 2. Jika berhasil, simpan data pengguna ke Firestore
                    val user = User(
                        uid = firebaseUser.uid,
                        name = name,
                        email = email,
                        createdAt = FieldValue.serverTimestamp()
                    )
                    // Dokumen akan dinamai sesuai dengan UID pengguna
                    db.collection("users").document(firebaseUser.uid).set(user).await()
                    _authSuccess.value = true
                    Log.d("AuthViewModel", "Registration and data save successful.")
                } else {
                    _error.value = "Failed to create user."
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("AuthViewModel", "Registration failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk Login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authSuccess.value = true
                Log.d("AuthViewModel", "Login successful.")
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("AuthViewModel", "Login failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk mereset status setelah navigasi
    fun resetAuthStatus() {
        _authSuccess.value = false
        _error.value = null
    }
}
