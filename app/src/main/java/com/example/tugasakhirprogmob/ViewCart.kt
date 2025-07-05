package com.example.tugasakhirprogmob

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.viewmodel.CartItem
import com.example.tugasakhirprogmob.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewCartScreen(
    navController: NavController, // Tambahkan ini
    onBackClick: () -> Unit,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    val subtotal by cartViewModel.subtotal.collectAsStateWithLifecycle()
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Cart",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keranjang Anda Kosong", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 24.dp)
                ) {
                    items(cartItems, key = { it.id }) { item ->
                        DynamicCartItem(
                            cartItem = item,
                            onIncrease = { cartViewModel.updateQuantity(item.id, item.quantity + 1) },
                            onDecrease = { cartViewModel.updateQuantity(item.id, item.quantity - 1) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            // Bagian Subtotal dan Tombol "Proceed" ditempatkan di bawah
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Subtotal", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = formatCurrency.format(subtotal), color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("checkout") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    // Tombol nonaktif jika keranjang kosong
                    enabled = cartItems.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "Proceed to Checkout", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}


@Composable
fun DynamicCartItem(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F7F9), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cartItem.imageUrl,
            contentDescription = cartItem.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cartItem.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCurrency.format(cartItem.price),
                fontSize = 16.sp,
                color = Color.Black
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 8.dp)
        ) {
            IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "Increase quantity"
                )
            }
            Text(
                text = cartItem.quantity.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.minus),
                    contentDescription = "Decrease quantity"
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ViewCartScreenPreview() {
    TugasAkhirProgmobTheme {
        // Kita buat NavController palsu (dummy) khusus untuk preview
        val dummyNavController = rememberNavController()
        ViewCartScreen(
            navController = dummyNavController, // Berikan dummy NavController
            onBackClick = {}
        )
    }
}