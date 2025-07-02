package com.example.tugasakhirprogmob

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.ui.components.BottomNavBar
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreateScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    productViewModel: ProductViewModel = viewModel() // Mengambil instance dari ViewModel
) {
    val context = LocalContext.current

    // State holders untuk semua input field UI
    var productName by remember { mutableStateOf("") }
    var askingPrice by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Category") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Mengamati state dari ViewModel
    val isLoading by productViewModel.isLoading.collectAsState()
    val isSuccess by productViewModel.isSuccess.collectAsState()

    // Launcher untuk memilih gambar dari galeri
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Efek ini akan berjalan ketika `isSuccess` menjadi true
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(context, "Product added successfully!", Toast.LENGTH_SHORT).show()
            productViewModel.resetSuccessState() // Reset state agar tidak ter-trigger lagi
            onBackClick() // Kembali ke halaman sebelumnya setelah berhasil
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text="Add Product", fontWeight = FontWeight.SemiBold) },
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
            // Jika Anda memiliki SearchBottomNavBar, Anda bisa uncomment ini
            // SearchBottomNavBar(selectedItem = 2)
            BottomNavBar(navController = navController)
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    item {
                        ImageUploader(
                            imageUri = imageUri,
                            onClick = { imagePickerLauncher.launch("image/*") }
                        )
                    }

                    item {
                        TitledTextField(title = "Product Name", value = productName, onValueChange = { productName = it }, placeholder = "Product Name")
                    }

                    item {
                        TitledTextField(title = "Asking Price", value = askingPrice, onValueChange = { askingPrice = it }, placeholder = "0.00", leadingText = "Rp.", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                CategoryDropdown(selectedCategory = selectedCategory, onCategorySelected = { selectedCategory = it })
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                TitledTextField(title = "Brand", value = brand, onValueChange = { brand = it }, placeholder = "Brand Name")
                            }
                        }
                    }

                    item {
                        TitledTextField(title = "Description", value = description, onValueChange = { description = it }, placeholder = "Lorem ipsum dolor sit amet...", singleLine = false, modifier = Modifier.height(200.dp))
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                Button(
                    onClick = {
                        // Memanggil fungsi ViewModel untuk menambah produk
                        productViewModel.addProduct(
                            context = context,
                            name = productName,
                            priceStr = askingPrice,
                            brand = brand,
                            category = selectedCategory,
                            description = description,
                            imageUri = imageUri
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = !isLoading // Tombol dinonaktifkan saat sedang loading
                ) {
                    Text(text = "Add Listing", fontSize = 16.sp, color = Color.White)
                }
            }

            // Menampilkan indikator loading di tengah layar jika isLoading true
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ImageUploader(imageUri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(500.dp, 200.dp) // Ukuran dari file Anda
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Selected product image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
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
    title: String, value: String, onValueChange: (String) -> Unit, placeholder: String,
    modifier: Modifier = Modifier, leadingText: String? = null, singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
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
        val dummyNavController = rememberNavController()
        ProductCreateScreen(
            navController = dummyNavController,
            onBackClick = {}
        )
    }
}