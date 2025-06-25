package com.example.tugasakhirprogmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation


class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Di dalam rumah ini, kita meletakkan perabotan kita
        setContent {
            TugasAkhirProgmobTheme {
                // Memanggil fungsi Composable yang sudah Anda buat
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF7BA9C9), // warna atas (rgba(123, 169, 201, 1))
                        Color(0xFF7A98AC)  // warna bawah (rgba(122, 152, 172, 1))
                    )
                )
            )
    ) {
        // Status bar bisa di-skip, nanti icon bisa pakai Image
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo image
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Edge Logo",
                    modifier = Modifier.size(32.dp) // ukurannya bisa diubah sesuai kebutuhan
                )

                Spacer(modifier = Modifier.width(8.dp)) // spasi antara logo dan teks

                Text(
                    text = "Edge",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }


            Spacer(modifier = Modifier.height(64.dp))

            // Text judul
            Text(
                text = "Log in",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your email to Log in for this app",
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Input email
            BasicTextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                decorationBox = { innerTextField ->
                    Box {
                        if (email.isEmpty()) {
                            Text(
                                text = "email@domain.com",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField() // di sini teks ketikanmu akan muncul
                    }
                }
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Input password
            BasicTextField(
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                decorationBox = { innerTextField ->
                    Box {
                        if (password.isEmpty()) {
                            Text(
                                text = "password",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Continue Button
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F586A)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Continue",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign up
            Text(
                text = "Or Sign up",
                color = Color.White,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Divider or
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFE6E6E6))
                )
                Text(
                    text = " or ",
                    color = Color.White
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFFE6E6E6))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Google Login
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                // Ikon + teks di dalam Row agar ikon dan teks sejajar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google), // ganti sesuai nama file
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue with Google",
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }


            // Terms
            Text(
                text = "By clicking continue, you agree to our Terms of Service and Privacy Policy",
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun LoginScreenPreview() {
    TugasAkhirProgmobTheme {
        LoginScreen()
    }
}
