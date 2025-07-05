package com.example.tugasakhirprogmob.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

// Data class untuk item di keranjang
data class CartItem(
    val id: String = "",
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    var quantity: Int = 1
)

// Data class untuk merepresentasikan sebuah pesanan
data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val status: String = "Pending",
    @ServerTimestamp
    val createdAt: Date? = null
)


class CartViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var cartListener: ListenerRegistration? = null

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal

    // --- TAMBAHAN BARU ---
    private val _orderPlacedSuccessfully = MutableStateFlow(false)
    val orderPlacedSuccessfully: StateFlow<Boolean> = _orderPlacedSuccessfully
    // ---------------------

    init {
        listenForCartChanges()
    }

    // --- FUNGSI BARU UNTUK MEMBUAT PESANAN ---
    fun placeOrder() {
        val userId = auth.currentUser?.uid
        if (userId == null || _cartItems.value.isEmpty()) {
            return // Jangan lakukan apa-apa jika user tidak login atau keranjang kosong
        }

        viewModelScope.launch {
            try {
                // 1. Buat objek Order baru
                val newOrder = Order(
                    userId = userId,
                    items = _cartItems.value, // Salin semua item dari keranjang
                    totalPrice = _subtotal.value,
                    status = "Pending"
                )

                // 2. Simpan order baru ke koleksi 'orders'
                db.collection("orders").add(newOrder).await()
                Log.d("CartViewModel", "Pesanan berhasil dibuat.")

                // 3. Hapus semua item dari keranjang pengguna
                val cartCollection = db.collection("users").document(userId).collection("cart")
                val currentCartItems = cartCollection.get().await()
                for (document in currentCartItems.documents) {
                    document.reference.delete().await()
                }
                Log.d("CartViewModel", "Keranjang berhasil dikosongkan.")

                // 4. Beri tahu UI bahwa proses berhasil
                _orderPlacedSuccessfully.value = true

            } catch (e: Exception) {
                Log.e("CartViewModel", "Error saat membuat pesanan", e)
            }
        }
    }

    fun resetOrderStatus() {
        _orderPlacedSuccessfully.value = false
    }
    // ------------------------------------------

    private fun listenForCartChanges() {
        val userId = auth.currentUser?.uid ?: return
        cartListener?.remove()
        cartListener = db.collection("users").document(userId).collection("cart")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CartViewModel", "Error listening for cart changes", error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CartItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _cartItems.value = items
                calculateSubtotal()
            }
    }

    private fun calculateSubtotal() {
        val total = _cartItems.value.sumOf { it.price * it.quantity }
        _subtotal.value = total
    }

    fun addToCart(product: Product) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val cartCollection = db.collection("users").document(userId).collection("cart")
                val existingItemQuery = cartCollection.whereEqualTo("productId", product.id).get().await()
                if (existingItemQuery.isEmpty) {
                    val newItem = CartItem(productId = product.id, name = product.name, price = product.price, imageUrl = product.imageUrls.firstOrNull() ?: product.imageUrl ?: "", quantity = 1)
                    cartCollection.add(newItem).await()
                } else {
                    val docId = existingItemQuery.documents.first().id
                    val currentQuantity = existingItemQuery.documents.first().getLong("quantity")?.toInt() ?: 0
                    cartCollection.document(docId).update("quantity", currentQuantity + 1).await()
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error saat menambahkan ke keranjang", e)
            }
        }
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        val userId = auth.currentUser?.uid ?: return
        val cartItemRef = db.collection("users").document(userId).collection("cart").document(cartItemId)
        if (newQuantity > 0) {
            cartItemRef.update("quantity", newQuantity)
        } else {
            cartItemRef.delete()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cartListener?.remove()
    }
}