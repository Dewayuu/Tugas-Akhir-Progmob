package com.example.tugasakhirprogmob

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


class Login : ComponentActivity() {
    // Tambahkan GoogleSignInClient di level Activity
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Konfigurasi Google Sign-In, sama seperti di Register.kt
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            TugasAkhirProgmobTheme {
                LoginScreen(
                    googleSignInClient = googleSignInClient, // Teruskan client ke Composable
                    onSignUpClick = {
                        startActivity(Intent(this, Register::class.java))
                        finish()
                    },
                    onLoginSuccess = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    googleSignInClient: GoogleSignInClient, // Terima client
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading by authViewModel.isLoading.collectAsState()
    val authSuccess by authViewModel.authSuccess.collectAsState()
    val error by authViewModel.error.collectAsState()

    // Launcher untuk menangani hasil dari Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken!!
                authViewModel.signInWithGoogle(idToken)
            } catch (e: ApiException) {
                Log.w("LoginScreen", "Google sign in failed", e)
                Toast.makeText(context, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Menampilkan Toast untuk error
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            authViewModel.resetAuthStatus()
        }
    }

    // Navigasi saat login berhasil
    LaunchedEffect(authSuccess) {
        if (authSuccess) {
            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
            authViewModel.resetAuthStatus()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF7BA9C9), Color(0xFF7A98AC))
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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

            // Email Input
            InputField(value = email, onValueChange = { email = it }, placeholder = "email@domain.com")
            Spacer(modifier = Modifier.height(16.dp))
            // Password Input
            InputField(value = password, onValueChange = { password = it }, placeholder = "password", isPassword = true)
            Spacer(modifier = Modifier.height(24.dp))

            // Continue (Login) Button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    authViewModel.login(email, password)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F586A)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !isLoading
            ) {
                Text(text = "Continue", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Link ke Halaman Sign Up
            Text(
                text = "Don't have an account? Sign up",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onSignUpClick() }
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
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
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
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(alignment = Alignment.Center),
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun LoginScreenPreview() {
    TugasAkhirProgmobTheme {
        // PERBAIKAN: Berikan nilai kosong untuk parameter lambda di preview
        LoginScreen(
            onSignUpClick = {},
            onLoginSuccess = {},
            authViewModel = TODO(),
            googleSignInClient = TODO()
        )
    }
}
