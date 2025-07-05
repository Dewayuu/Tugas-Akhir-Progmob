package com.example.tugasakhirprogmob.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CartViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var cartListener: ListenerRegistration? = null

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal

    init {
        listenForCartChanges()
    }

    private fun listenForCartChanges() {
        val userId = auth.currentUser?.uid ?: return
        // Hentikan listener lama jika ada untuk mencegah kebocoran memori
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
                    val newItem = CartItem(
                        productId = product.id,
                        name = product.name,
                        price = product.price,
                        imageUrl = product.imageUrls.firstOrNull() ?: product.imageUrl ?: "",
                        quantity = 1
                    )
                    cartCollection.add(newItem).await()
                    Log.d("CartViewModel", "Produk baru '${product.name}' ditambahkan ke keranjang.")
                } else {
                    val docId = existingItemQuery.documents.first().id
                    val currentQuantity = existingItemQuery.documents.first().getLong("quantity")?.toInt() ?: 0
                    cartCollection.document(docId).update("quantity", currentQuantity + 1).await()
                    Log.d("CartViewModel", "Kuantitas produk '${product.name}' diperbarui.")
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
            // Jika kuantitas menjadi 0 atau kurang, hapus item dari keranjang
            cartItemRef.delete()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Penting: Hapus listener saat ViewModel tidak lagi digunakan untuk mencegah memory leak
        cartListener?.remove()
    }
}