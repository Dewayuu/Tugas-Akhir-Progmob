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
import androidx.navigation.compose.rememberNavController
import com.example.tugasakhirprogmob.ui.components.BottomNavBar
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme

// Nama fungsi diubah dan sekarang menerima NavController
@Composable
fun UserProfileScreen(navController: NavController) {
    Scaffold(
        // Kita gunakan BottomNavBar bersama agar navigasi konsisten
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = Color(0xFFEFF3F6)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Terapkan padding dari Scaffold
                .verticalScroll(rememberScrollState())
        ) {
            // Header sederhana untuk halaman profil
            TopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
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
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("4.5 • 3 Reviews", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("2y, 4mo Joined", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        IconButton(onClick = { /* TODO: Navigasi ke halaman edit profil */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        Row {
                            Box(modifier = Modifier.size(24.dp).background(Color.LightGray, shape = RoundedCornerShape(4.dp)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.size(24.dp).background(Color.LightGray, shape = RoundedCornerShape(4.dp)))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // About Card
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

            // Listing Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 24.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Listing", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray)
                            .clickable { /* TODO: Navigasi ke halaman 'Add Product' */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
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
        UserProfileScreen(navController = rememberNavController())
    }
}
