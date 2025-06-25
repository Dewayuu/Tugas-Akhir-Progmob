package com.example.tugasakhirprogmob

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme

@Composable
fun ViewCartScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Status bar dummy bisa Image kalau mau
        // Header
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                )
                Text(
                    text = "My Cart",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Cart Item 1
            CartItem(
                productName = "Basic T-Shirt",
                productPrice = "Rp100.000",
                quantity = 1,
                imageResId = R.drawable.uniqlo,
                modifier = Modifier.padding(horizontal = 16.dp) // jarak ke kiri & kanan
            )

            Spacer(Modifier.height(16.dp))

// Cart Item 2
            CartItem(
                productName = "Preloved Nike",
                productPrice = "Rp300.000",
                quantity = 1,
                imageResId = R.drawable.nike,
                modifier = Modifier.padding(horizontal = 16.dp) // jarak ke kiri & kanan
            )


            Spacer(modifier = Modifier.weight(1f))

            // Subtotal box
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
                    .width(360.dp)       // lebar spesifik
                    .height(70.dp)       // tinggi spesifik
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(5.dp),
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically // <-- ini kunci agar di tengah secara vertikal
                    ) {
                        Text(
                            text = "Subtotal",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Rp200.000",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }


            Spacer(Modifier.height(21.dp))

            // Proceed button
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Proceed",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun CartItem(
    productName: String,
    productPrice: String,
    quantity: Int,
    imageResId: Int,
    modifier: Modifier = Modifier // ← tambahkan parameter ini
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE8E8E8), RoundedCornerShape(10.dp)) // lebih light
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gambar produk
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(16.dp)) // jarak antar gambar dan teks

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
            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = quantity.toString(),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = painterResource(id = R.drawable.minus),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}






@Preview(showBackground = true, showSystemUi = false)
@Composable
fun ViewCartScreenPreview() {
    TugasAkhirProgmobTheme {
        ViewCartScreen()
    }
}
