package com.example.tugasakhirprogmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhirprogmob.R
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme

class Checkout : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Di dalam rumah ini, kita meletakkan perabotan kita
        setContent {
            TugasAkhirProgmobTheme {
                // Memanggil fungsi Composable yang sudah Anda buat
                CheckoutScreen()
            }
        }
    }
}

@Composable
fun CheckoutScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color.White)
                    .border(
                        width = 0.5.dp,
                        color = Color(0xFFE6E6E6),
                        shape = RoundedCornerShape(0.dp)
                    )
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
                    text = "Order",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ADDRESS section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ADDRESS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(200.dp) // beri lebar tetap agar teks bisa lebih ke kiri
                    ) {
                        Text(
                            text = "Add address",
                            fontSize = 15.sp,
                            color = Color(0xFF808080),
                            modifier = Modifier.weight(1f) // agar teks ambil ruang
                        )
                        Image(
                            painter = painterResource(id = R.drawable.kanan),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                HorizontalDivider(
                    color = Color(0xFFE6E6E6),
                    thickness = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DELIVERY",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(200.dp) // kasih lebar tetap
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start // agar teksnya rata kiri dalam box
                        ) {
                            Text(
                                text = "Standard",
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "3-4 working days",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.kanan),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                }
                HorizontalDivider(
                    color = Color(0xFFE6E6E6),
                    thickness = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Products list
            ProductList(
                listOf(
                    ProductData(
                        brand = "Brand",
                        productName = "Product name",
                        description = "Description",
                        price = "Rp100.000",
                        quantity = "1x",
                        imageResId = R.drawable.uniqlo
                    ),
                    ProductData(
                        brand = "Brand",
                        productName = "Product name",
                        description = "Description",
                        price = "Rp100.000",
                        quantity = "1x",
                        imageResId = R.drawable.nike
                    )
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Order summary
            OrderSummary(
                subtotal = "Rp100.000",
                delivery = "Free",
                platformFee = "Rp1.000",
                total = "Rp101.000"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Order button
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "By tapping on ‘Order’, you accept the terms of service from Edge",
                    fontSize = 11.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = { /* Order */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Order",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProductList(products: List<ProductData>) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        products.forEach { product ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically // ini penting!
            ) {
                // Gambar
                Image(
                    painter = painterResource(id = product.imageResId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)  // pastikan gambar persegi
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Column teks diberi tinggi sama seperti gambar
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .height(90.dp) // <- wajib agar center vertikal
                        .weight(1f)
                ) {
                    Text(
                        text = product.brand,
                        fontSize = 12.sp,
                        color = Color(0xFF808080)
                    )
                    Text(
                        text = product.productName,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = product.description,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = product.price,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                // Quantity
                Text(
                    text = product.quantity,
                    fontSize = 12.sp,
                    color = Color(0xFF6C6D6F),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}



@Composable
fun OrderSummary(
    subtotal: String,
    delivery: String,
    platformFee: String,
    total: String
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Tambahkan teks judul
        Text(
            text = "Total summary",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp) // jarak di bawahnya
        )

        // Lanjutkan summary row
        SummaryRow(label = "Subtotal (2)", value = subtotal)
        SummaryRow(label = "Delivery", value = delivery)
        SummaryRow(label = "Platform fee", value = platformFee)
        SummaryRow(label = "Total", value = total, isBold = true)
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Normal
        )
        Text(
            text = value,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Normal
        )
    }
    Spacer(Modifier.height(8.dp))
}

// Dummy product model
data class ProductData(
    val brand: String,
    val productName: String,
    val description: String,
    val price: String,
    val quantity: String,
    val imageResId: Int
)

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun CheckoutScreenPreview() {
    CheckoutScreen()
}

