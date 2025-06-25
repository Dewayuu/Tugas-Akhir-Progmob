package com.example.fashionpreloved.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fashionpreloved.R

@Composable
fun UserProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFEFF3F6)) // background abu muda dari UI
    ) {
        // 🔍 Search Bar + 🛒 Cart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search Edge") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { navController.navigate("cart") }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cart),
                    contentDescription = "Cart",
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // 🔘 Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 12.dp)
        ) {
            val filters = listOf("Filters", "Jacket", "Women’s Clothing")
            filters.forEach { text ->
                FilterChip(
                    label = { Text(text) },
                    onClick = { },
                    selected = false,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 👤 Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    tint = Color.Unspecified
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("User A", style = MaterialTheme.typography.titleMedium)
                    Text("Denpasar", style = MaterialTheme.typography.bodySmall)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("4.5 • 3 Reviews", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("2y, 4mo Joined", style = MaterialTheme.typography.bodySmall)
                }

                // Dua tombol kecil seperti pada UI (placeholder untuk action)
                Column(horizontalAlignment = Alignment.End) {
                    IconButton(onClick = { navController.navigate("edit_profile") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    Row {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 📝 About Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("About", style = MaterialTheme.typography.titleMedium)
                Text("Placeholder text", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📦 Listing Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, bottom = 24.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Listing", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                // Tombol Start Selling
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                        .clickable { /* TODO: start selling */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(24.dp)
                        )
                        Text("Start Selling")
                    }
                }
            }
        }
    }
}
