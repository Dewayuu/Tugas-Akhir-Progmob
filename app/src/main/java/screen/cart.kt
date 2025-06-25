package com.example.fashionpreloved.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape





@Composable
fun CartScreen(navController: NavController) {
    val cartItems = remember {
        mutableStateListOf(
            CartItem("Product name", 100_000),
            CartItem("Product name", 100_000)
        )
    }

    val subtotal = cartItems.sumOf { it.quantity * it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("My Cart", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(48.dp)) // placeholder for alignment
        }

        // Cart Items
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems) { item ->
                CartItemRow(item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subtotal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal", style = MaterialTheme.typography.bodyLarge)
            Text("Rp${subtotal}", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Proceed Button
        Button(
            onClick = { /* checkout */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Proceed", color = Color.White)
        }
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium)
                Text("Rp${item.price}", style = MaterialTheme.typography.bodySmall)
            }

            // Quantity controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (item.quantity > 1) item.quantity-- }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
                Text("${item.quantity}", modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                IconButton(onClick = { item.quantity++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
    }
}

// Model
data class CartItem(val name: String, val price: Int, var quantity: Int = 1)
