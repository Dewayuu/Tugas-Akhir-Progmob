package com.example.tugasakhirprogmob.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // --- FUNGSI BARU UNTUK GOOGLE SIGN-IN ---
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // Cek apakah pengguna baru atau sudah ada
                    val userDoc = db.collection("users").document(firebaseUser.uid).get().await()
                    if (!userDoc.exists()) {
                        // Pengguna baru: Simpan datanya ke Firestore
                        val newUser = User(
                            uid = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "Google User",
                            email = firebaseUser.email ?: "no-email@google.com",
                            createdAt = FieldValue.serverTimestamp()
                        )
                        db.collection("users").document(firebaseUser.uid).set(newUser).await()
                        Log.d("AuthViewModel", "New Google user created in Firestore.")
                    } else {
                        Log.d("AuthViewModel", "Existing Google user signed in.")
                    }
                    _authSuccess.value = true
                } else {
                    _error.value = "Google Sign-In failed."
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("AuthViewModel", "Google Sign-In failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Fungsi untuk Registrasi email/password
    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val user = User(
                        uid = firebaseUser.uid,
                        name = name,
                        email = email,
                        createdAt = FieldValue.serverTimestamp()
                    )
                    db.collection("users").document(firebaseUser.uid).set(user).await()
                    _authSuccess.value = true
                } else {
                    _error.value = "Failed to create user."
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk Login email/password
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetAuthStatus() {
        _authSuccess.value = false
        _error.value = null
    }
}
