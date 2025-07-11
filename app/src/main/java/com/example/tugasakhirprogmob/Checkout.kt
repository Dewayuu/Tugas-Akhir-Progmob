package com.example.tugasakhirprogmob

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.viewmodel.CartItem
import com.example.tugasakhirprogmob.viewmodel.CartViewModel
import com.example.tugasakhirprogmob.viewmodel.ProfileViewModel
import java.text.NumberFormat
import java.util.*
import com.example.tugasakhirprogmob.viewmodel.CartSelectionHolder
import androidx.compose.runtime.mutableStateOf



// Data class untuk opsi pengiriman
data class ShippingOption(val name: String, val duration: String, val cost: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {

    val context = LocalContext.current
    //val cartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    //val subtotal by cartViewModel.subtotal.collectAsStateWithLifecycle()
    val orderPlaced by cartViewModel.orderPlacedSuccessfully.collectAsStateWithLifecycle()
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()

    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    var isLoading by remember { mutableStateOf(false) }

    // --- STATE UNTUK ALAMAT & PENGIRIMAN ---
    var showAddressDialog by remember { mutableStateOf(false) }
    var manualAddress by remember { mutableStateOf("") }

    val shippingOptions = listOf(
        ShippingOption("Reguler", "3-5 Hari Kerja", 0.0),
        ShippingOption("Express", "1-2 Hari Kerja", 15000.0)
    )
    var selectedShipping by remember { mutableStateOf(shippingOptions.first()) }
    val selectedItems = CartSelectionHolder.selectedItems
    //val selectedItems = remember { CartSelectionHolder.selectedItems }
    val subtotal = selectedItems.sumOf { it.price * it.quantity }
    val total = subtotal + selectedShipping.cost + 1000.0

    //val total = subtotal + selectedShipping.cost + 1000.0 // +1000 untuk platform fee
    // ------------------------------------------

    // Ambil data profil saat layar pertama kali dibuka
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    // Navigasi setelah pesanan berhasil dibuat
    LaunchedEffect(orderPlaced) {
        if (orderPlaced) {
            isLoading = false
            Toast.makeText(context, "Pesanan berhasil dibuat!", Toast.LENGTH_LONG).show()
            cartViewModel.resetOrderStatus()
            navController.navigate("order_success") {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ringkasan Pesanan", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Back")
                    }
                },
                actions = { Spacer(Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F7F9)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        AddressSection(
                            profileAddress = userProfile?.address,
                            manualAddress = manualAddress,
                            onEditClick = { showAddressDialog = true }
                        )
                    }
                    item { DeliverySection(options = shippingOptions, selected = selectedShipping, onOptionSelected = { selectedShipping = it }) }
                    item {
                        Text(
                            "Produk Dipesan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(selectedItems) { item ->
                        CheckoutProductItem(item, formatCurrency)
                    }
                }

                // Bagian bawah layar
                Column(Modifier.background(Color.White)) {
                    OrderSummary(
                        itemCount = selectedItems.sumOf { it.quantity },
                        subtotal = subtotal,
                        delivery = selectedShipping.cost,
                        platformFee = 1000.0,
                        total = total,
                        formatCurrency = formatCurrency
                    )
                    Button(
                        onClick = {
                            val finalAddress = if (manualAddress.isNotBlank()) manualAddress else userProfile?.address
                            if (finalAddress.isNullOrBlank()) {
                                Toast.makeText(context, "Mohon atur alamat pengiriman", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            isLoading = true
                            cartViewModel.placeOrder(
                                alamat = finalAddress,
                                metodePengiriman = selectedShipping.name,
                                totalDenganPengiriman = total
                            )
                        },
                        enabled = selectedItems.isNotEmpty() && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(48.dp)
                    ) {
                        Text("Buat Pesanan", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            if (showAddressDialog) {
                ManualAddressDialog(
                    onDismissRequest = { showAddressDialog = false },
                    onAddressConfirmed = {
                        manualAddress = it
                        showAddressDialog = false
                    },
                    initialAddress = if (manualAddress.isNotBlank()) manualAddress else (userProfile?.address ?: "")
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun AddressSection(profileAddress: String?, manualAddress: String, onEditClick: () -> Unit) {
    val displayAddress = if (manualAddress.isNotBlank()) manualAddress else profileAddress
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Alamat Pengiriman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onEditClick) {
                Text("Ubah")
            }
        }
        Text(displayAddress ?: "Alamat belum diatur.", color = if (displayAddress.isNullOrBlank()) Color.Red else Color.Black)
    }
}

@Composable
private fun DeliverySection(
    options: List<ShippingOption>,
    selected: ShippingOption,
    onOptionSelected: (ShippingOption) -> Unit
) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text("Metode Pengiriman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (option == selected),
                        onClick = { onOptionSelected(option) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selected),
                    onClick = { onOptionSelected(option) }
                )
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(option.name, fontWeight = FontWeight.SemiBold)
                    Text(option.duration, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Text(
                    if (option.cost == 0.0) "Gratis" else formatCurrency.format(option.cost),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun CheckoutProductItem(cartItem: CartItem, formatCurrency: NumberFormat) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cartItem.imageUrl,
            contentDescription = cartItem.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(cartItem.name, fontWeight = FontWeight.SemiBold)
            Text("${cartItem.quantity} produk", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(formatCurrency.format(cartItem.price * cartItem.quantity))
    }
}

@Composable
private fun OrderSummary(
    itemCount: Int,
    subtotal: Double,
    delivery: Double,
    platformFee: Double,
    total: Double,
    formatCurrency: NumberFormat
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Ringkasan Belanja", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        SummaryRow(label = "Subtotal ($itemCount produk)", value = formatCurrency.format(subtotal))
        SummaryRow(label = "Biaya Pengiriman", value = if (delivery == 0.0) "Gratis" else formatCurrency.format(delivery))
        SummaryRow(label = "Biaya Jasa Aplikasi", value = formatCurrency.format(platformFee))
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SummaryRow(label = "Total Belanja", value = formatCurrency.format(total), isBold = true)
    }
}

@Composable
private fun SummaryRow(label: String, value: String, isBold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = if (isBold) Color.Black else Color.Gray)
        Text(text = value, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualAddressDialog(
    onDismissRequest: () -> Unit,
    onAddressConfirmed: (String) -> Unit,
    initialAddress: String
) {
    var newAddress by remember { mutableStateOf(initialAddress) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Masukkan Alamat Pengiriman") },
        text = {
            OutlinedTextField(
                value = newAddress,
                onValueChange = { newAddress = it },
                label = { Text("Alamat Lengkap") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onAddressConfirmed(newAddress) }) {
                Text("Gunakan Alamat Ini")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Batal")
            }
        }
    )
}
