package com.example.tugasakhirprogmob

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme

// PENTING: Untuk menggunakan AsyncImage, tambahkan dependensi Coil ke file build.gradle.kts (Module: app) Anda:
// implementation("io.coil-kt:coil-compose:2.6.0")

class ProductCreatePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TugasAkhirProgmobTheme {
                ProductCreateScreen(onBackClick = { /* Handle back logic here */ })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreateScreen(onBackClick: () -> Unit) {
    // State holders untuk semua input field
    var productName by remember { mutableStateOf("") }
    var askingPrice by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Category") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher untuk memilih gambar dari galeri
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* No title needed as per screenshot */ },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        bottomBar = {
            // Menggunakan kembali BottomNavBar yang ada, dengan item 'add' yang dipilih
            // Pastikan Anda memiliki implementasi SearchBottomNavBar di proyek Anda
            // SearchBottomNavBar(selectedItem = 2)
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // LazyColumn mengambil semua ruang yang tersedia
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // 1. Image Uploader
                item {
                    ImageUploader(
                        imageUri = imageUri,
                        onClick = {
                            // Membuka pemilih gambar
                            imagePickerLauncher.launch("image/*")
                        }
                    )
                }


                // 2. Product Name
                item {
                    TitledTextField(
                        title = "Product Name",
                        value = productName,
                        onValueChange = { productName = it },
                        placeholder = "Product Name"
                    )
                }

                // 3. Asking Price
                item {
                    TitledTextField(
                        title = "Asking Price",
                        value = askingPrice,
                        onValueChange = { askingPrice = it },
                        placeholder = "0,000,00",
                        leadingText = "Rp.",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // 4. Category & Brand
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            CategoryDropdown(
                                selectedCategory = selectedCategory,
                                onCategorySelected = { selectedCategory = it }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            TitledTextField(
                                title = "Brand",
                                value = brand,
                                onValueChange = { brand = it },
                                placeholder = "Brand Name"
                            )
                        }
                    }
                }

                // 5. Description
                item {
                    TitledTextField(
                        title = "Description",
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Lorem ipsum dolor sit amet...",
                        singleLine = false,
                        modifier = Modifier.height(150.dp)
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Tombol "Add Listing" di bagian bawah, di atas BottomNavBar
            Button(
                onClick = { /* TODO: Handle Add Listing Logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Add Listing", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun ImageUploader(imageUri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(16.dp)) // Clip bentuk sebelum border
            .background(Color.White) // Menambahkan latar belakang putih
            .border(
                width = 2.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick), // Gunakan lambda onClick yang diteruskan
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            // Jika ada gambar yang dipilih, tampilkan
            AsyncImage(
                model = imageUri,
                contentDescription = "Selected product image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Crop agar gambar mengisi Box
            )
        } else {
            // Jika tidak, tampilkan placeholder
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Image",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Gray
                )
                Text(text = "Add Image", color = Color.Gray)
            }
        }
    }
}

@Composable
fun TitledTextField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingText: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            leadingIcon = if (leadingText != null) {
                { Text(text = leadingText, modifier = Modifier.padding(start = 12.dp)) }
            } else null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color.Black,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Electronics", "Fashion", "Home & Kitchen", "Books")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Category", fontWeight = FontWeight.SemiBold)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth(),
//                    .menuAnchor(),
                leadingIcon = {
//                    Icon(
////                        painter = painterResource(id = R.drawable.help), // Pastikan resource icon ini ada
//                        contentDescription = null,
//                        tint = Color.Gray
//                    )
                },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.LightGray,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category) },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductCreateScreenPreview() {
    TugasAkhirProgmobTheme {
        ProductCreateScreen(onBackClick = {})
    }
}