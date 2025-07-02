@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tugasakhirprogmob

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tugasakhirprogmob.ui.components.BottomNavBar
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.navigation.compose.rememberNavController


// Nama fungsi diubah dan sekarang menerima NavController
@Composable
fun UserProfileScreen(
    navController: NavController,
    onCartClick: () -> Unit
) {
    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Profile") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.Transparent // Atau warna yang Anda inginkan
//                )
//            )
//        },
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = Color(0xFFEFF3F6)
    ) { innerPadding -> // innerPadding sekarang akan berisi padding untuk topBar dan bottomBar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Terapkan padding yang benar dari Scaffold
                .verticalScroll(rememberScrollState())
        ) {

            // 🔍 Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Mungkin Anda ingin menambahkan sedikit padding di atas
                    .padding(top = 8.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search Edge", color = Color.Gray, modifier = Modifier.weight(1f))
                IconButton(onClick = onCartClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.cart),
                        contentDescription = "Cart",
                        modifier = Modifier.size(28.dp) // Ukuran ikon disamakan
                    )
                }
            }

            // 🧷 Filter Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                listOf("Filters", "Jacket", "Women’s Clothing").forEach {
                    AssistChip(
                        onClick = { /* TODO */ },
                        label = { Text(it) },
                        modifier = Modifier.padding(end = 8.dp)
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
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
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("4.5", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("• 3 Reviews", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("2y, 4mo Joined", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        IconButton(onClick = { /* TODO: Go to edit profile */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        Row {
                            repeat(2) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ℹ️ About Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
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
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Listing", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray)
                            .clickable { /* TODO: Go to Add Product screen */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
                            Text("Start Selling")
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    TugasAkhirProgmobTheme {
        // Preview sekarang memanggil fungsi yang benar dengan NavController bohongan
        UserProfileScreen(navController = rememberNavController(),
            onCartClick = {})
    }
}




