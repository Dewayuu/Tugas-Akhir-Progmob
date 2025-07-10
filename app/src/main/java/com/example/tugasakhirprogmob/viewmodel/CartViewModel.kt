package com.example.tugasakhirprogmob.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

// --- DATA CLASS UNTUK KERANJANG & PESANAN HANYA DI SINI ---
data class CartItem(
    val id: String = "",
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    var quantity: Int = 1,
    val availableStock: Int = 1,
    val sellerId: String = ""
)

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    var status: String = "Pending",
    @ServerTimestamp
    val createdAt: Date? = null,
    val receiverName: String? = null,
    var alamatPengiriman: String = "",
    var statusPengiriman: String = "Menunggu Pembayaran",
    var nomorResi: String? = null,
    var metodePengiriman: String = ""
)
// -----------------------------------------------------------------

class CartViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var cartListener: ListenerRegistration? = null

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal

    private val _orderPlacedSuccessfully = MutableStateFlow(false)
    val orderPlacedSuccessfully: StateFlow<Boolean> = _orderPlacedSuccessfully

    init {
        listenForCartChanges()
    }

    fun placeOrder(alamat: String, metodePengiriman: String, totalDenganPengiriman: Double) {
        val userId = auth.currentUser?.uid
        if (userId == null || _cartItems.value.isEmpty()) {
            return
        }

        viewModelScope.launch {
            // Lepas listener untuk mencegah deadlock
            cartListener?.remove()

            val itemsToOrder = _cartItems.value
            try {
                db.runTransaction { transaction ->
                    val productRefs = itemsToOrder.map { db.collection("products").document(it.productId) }
                    val productSnapshots = productRefs.map { ref -> transaction.get(ref) }

                    // Validasi stok
                    for ((index, item) in itemsToOrder.withIndex()) {
                        val snapshot = productSnapshots[index]
                        val currentStock = snapshot.getLong("stock")?.toInt() ?: 0
                        if (currentStock < item.quantity) {
                            throw Exception("Stok untuk '${item.name}' tidak mencukupi.")
                        }
                    }

                    // Buat pesanan baru
                    val newOrderRef = db.collection("orders").document()
                    val newOrder = Order(
                        userId = userId, items = itemsToOrder, totalPrice = totalDenganPengiriman,
                        status = "Pending", alamatPengiriman = alamat, metodePengiriman = metodePengiriman,
                        statusPengiriman = "Menunggu Pembayaran", receiverName = auth.currentUser?.displayName ?: "Pengguna"
                    )
                    transaction.set(newOrderRef, newOrder)

                    // Kurangi stok produk
                    for ((index, item) in itemsToOrder.withIndex()) {
                        transaction.update(productRefs[index], "stock", FieldValue.increment(-item.quantity.toLong()))
                    }

                    // Kosongkan keranjang
                    val cartCollectionRef = db.collection("users").document(userId).collection("cart")
                    itemsToOrder.forEach { item -> transaction.delete(cartCollectionRef.document(item.id)) }
                }.await()

                Log.d("CartViewModel", "Transaksi BERHASIL.")
                _orderPlacedSuccessfully.value = true

            } catch (e: Exception) {
                Log.e("CartViewModel", "Operasi checkout GAGAL: ${e.message}")
            } finally {
                // Pasang kembali listener setelah selesai
                listenForCartChanges()
            }
        }
    }

    fun resetOrderStatus() {
        _orderPlacedSuccessfully.value = false
    }

    private fun listenForCartChanges() {
        val userId = auth.currentUser?.uid ?: return
        cartListener?.remove()
        cartListener = db.collection("users").document(userId).collection("cart")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CartViewModel", "Error listening: ", error)
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

    fun addToCartWithQuantity(product: Product, quantity: Int) {
        val userId = auth.currentUser?.uid ?: return
        if (product.sellerId == userId) {
            Log.w("CartViewModel", "Penjual tidak dapat membeli produknya sendiri.")
            return
        }
        if (product.stock < quantity) {
            Log.e("CartViewModel", "Kuantitas ($quantity) melebihi stok (${product.stock}).")
            return
        }
        viewModelScope.launch {
            try {
                val cartCollection = db.collection("users").document(userId).collection("cart")
                val existingItemQuery = cartCollection.whereEqualTo("productId", product.id).get().await()
                if (existingItemQuery.isEmpty) {
                    val newItem = CartItem(
                        productId = product.id, name = product.name, price = product.price,
                        imageUrl = product.imageUrls.firstOrNull() ?: product.imageUrl ?: "",
                        quantity = quantity, availableStock = product.stock, sellerId = product.sellerId
                    )
                    cartCollection.add(newItem).await()
                } else {
                    val docId = existingItemQuery.documents.first().id
                    val currentQuantity = existingItemQuery.documents.first().getLong("quantity")?.toInt() ?: 0
                    if (currentQuantity + quantity <= product.stock) {
                        cartCollection.document(docId).update("quantity", FieldValue.increment(quantity.toLong())).await()
                    } else {
                        Log.w("CartViewModel", "Gagal menambah, total kuantitas akan melebihi stok.")
                    }
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error saat addToCartWithQuantity", e)
            }
        }
    }

    fun addToCart(product: Product) {
        addToCartWithQuantity(product, 1)
    }

    fun updateQuantity(cartItemId: String, productId: String, newQuantity: Int) {
        val userId = auth.currentUser?.uid ?: return
        val cartItemRef = db.collection("users").document(userId).collection("cart").document(cartItemId)
        viewModelScope.launch {
            try {
                if (newQuantity <= 0) {
                    cartItemRef.delete().await()
                    return@launch
                }
                val productRef = db.collection("products").document(productId).get().await()
                val productStock = productRef.getLong("stock")?.toInt() ?: 0
                if (newQuantity <= productStock) {
                    cartItemRef.update("quantity", newQuantity).await()
                } else {
                    Log.w("CartViewModel", "Kuantitas melebihi stok yang tersedia ($productStock).")
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Gagal memperbarui kuantitas", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cartListener?.remove()
    }
}