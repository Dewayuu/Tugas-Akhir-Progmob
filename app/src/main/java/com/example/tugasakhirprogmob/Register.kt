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
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.tugasakhirprogmob.viewmodel.AuthViewModel
import com.google.firebase.FirebaseApp

class Register : ComponentActivity() {
    // Pindahkan GoogleSignInClient ke level Activity agar bisa diakses
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Konfigurasi Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ambil dari strings.xml
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            TugasAkhirProgmobTheme {
                RegisterScreen(
                    googleSignInClient = googleSignInClient, // Teruskan client ke Composable
                    onLoginClick = {
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    },
                    onRegisterSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    }
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel = viewModel(),
    googleSignInClient: GoogleSignInClient, // Terima client
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isLoading by authViewModel.isLoading.collectAsState()
    val authSuccess by authViewModel.authSuccess.collectAsState()
    val error by authViewModel.error.collectAsState()

    // --- LAUNCHER BARU UNTUK GOOGLE SIGN-IN ---
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
                Log.w("RegisterScreen", "Google sign in failed", e)
                Toast.makeText(context, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Menampilkan Toast untuk error
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            authViewModel.resetAuthStatus() // Reset error setelah ditampilkan
        }
    }

    // Navigasi saat registrasi berhasil
    LaunchedEffect(authSuccess) {
        if (authSuccess) {
            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Edge",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Register",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter your email to Register for this app",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Name label
                Text(
                    text = "Name",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                InputField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Your full name"
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Email
                Text(
                    text = "Email",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                InputField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "email@domain.com"
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Password
                Text(
                    text = "Password",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                InputField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Your password",
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password
                Text(
                    text = "Re-type password",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                InputField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirm your password",
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        if (password != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        authViewModel.register(name, email, password)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F586A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !isLoading // Nonaktifkan tombol saat loading
                ) {
                    Text(text = "Register", color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Link ke Halaman Login
                Text(
                    text = "Already have an account? Log In",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onLoginClick() }
                )

                Spacer(modifier = Modifier.height(16.dp))

//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(
//                        text = "Continue as a guest",
//                        color = Color.White,
//                        fontSize = 12.sp
//                    )
//                    Text(
//                        text = "Log In",
//                        color = Color.White,
//                        fontSize = 12.sp
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(32.dp))

                // Divider
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
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !isLoading // Nonaktifkan saat sedang loading
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

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "By clicking continue, you agree to our Terms of Service and Privacy Policy",
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
        // Tampilkan loading indicator di tengah
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
    }
}

// Reusable input field
@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun RegisterScreenPreview() {
    TugasAkhirProgmobTheme {
        // PERBAIKAN: Berikan nilai kosong untuk parameter lambda di preview
        RegisterScreen(
            onLoginClick = {},
            onRegisterSuccess = {},
            authViewModel = TODO(),
            googleSignInClient = TODO()
        )
    }
}
