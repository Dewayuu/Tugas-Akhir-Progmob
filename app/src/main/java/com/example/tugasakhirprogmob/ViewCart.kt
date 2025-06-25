package com.example.tugasakhirprogmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewCartScreen(onBackClick: () -> Unit) {
    // PERBAIKAN UTAMA: Menggunakan Scaffold untuk struktur layar yang lebih aman
    Scaffold(
        topBar = {
            // TopBar adalah slot khusus di Scaffold untuk header
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
                    // Tombol kembali ditempatkan di slot khusus navigationIcon
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    // Spacer agar judul benar-benar di tengah
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        // Konten utama diletakkan di dalam Column dengan padding dari Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Padding ini penting agar konten tidak tertutup TopBar
        ) {
            Spacer(Modifier.height(24.dp))

            // LazyColumn untuk membuat konten bisa di-scroll jika itemnya banyak
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    CartItem(
                        productName = "Basic T-Shirt",
                        productPrice = "Rp100.000",
                        quantity = 1,
                        imageResId = R.drawable.uniqlo,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                item {
                    Spacer(Modifier.height(16.dp))
                }
                item {
                    CartItem(
                        productName = "Preloved Nike",
                        productPrice = "Rp300.000",
                        quantity = 1,
                        imageResId = R.drawable.nike,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Bagian Subtotal dan Tombol "Proceed" ditempatkan di bawah
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Subtotal box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(5.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Subtotal", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Rp400.000", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Proceed button
                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "Proceed", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}


@Composable
fun CartItem(
    productName: String,
    productPrice: String,
    quantity: Int,
    imageResId: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE8E8E8), RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = productName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = productPrice,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            IconButton(onClick = { /* TODO: Increase quantity */ }) {
                Image(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "Increase quantity"
                )
            }
            Text(
                text = quantity.toString(),
                fontSize = 14.sp
            )
            IconButton(onClick = { /* TODO: Decrease quantity */ }) {
                Image(
                    painter = painterResource(id = R.drawable.minus),
                    contentDescription = "Decrease quantity"
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = false)
@Composable
fun ViewCartScreenPreview() {
    TugasAkhirProgmobTheme {
        ViewCartScreen(onBackClick = {})
    }
}
