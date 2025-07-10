package com.example.tugasakhirprogmob.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

// DATA CLASS UNTUK NOTIFIKASI
data class InAppNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    val isRead: Boolean = false,
    val orderId: String? = null
)

// VIEWMODEL UNTUK MENGAMBIL NOTIFIKASI
class NotificationViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _notifications = MutableStateFlow<List<InAppNotification>>(emptyList())
    val notifications: StateFlow<List<InAppNotification>> = _notifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("notifications")
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val notificationList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(InAppNotification::class.java)?.copy(id = doc.id)
                }
                _notifications.value = notificationList
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Gagal mengambil notifikasi", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                db.collection("notifications").document(notificationId).update("isRead", true).await()
                fetchNotifications()
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Gagal menandai notifikasi sebagai dibaca", e)
            }
        }
    }
}