package com.example.tugasakhirprogmob

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val userProfile by profileViewModel.userProfile.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val updateSuccess by profileViewModel.updateSuccess.collectAsState()

    var username by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var profilePicUri by remember { mutableStateOf<Uri?>(null) }
    var bannerUri by remember { mutableStateOf<Uri?>(null) }
    var removeProfilePic by remember { mutableStateOf(false) }
    var removeBanner by remember { mutableStateOf(false) }

    val profileImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            profilePicUri = uri
            if (uri != null) removeProfilePic = false
        }
    )

    val bannerImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            bannerUri = uri
            if (uri != null) removeBanner = false
        }
    )

    LaunchedEffect(userProfile) {
        userProfile?.let {
            username = it.name
            bio = it.bio ?: ""
            address = it.address ?: ""
            phoneNumber = it.phoneNumber ?: ""
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            profileViewModel.resetUpdateStatus()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (username.isBlank()) {
                        Toast.makeText(context, "Username cannot be empty!", Toast.LENGTH_SHORT).show()
                    } else {
                        profileViewModel.updateProfile(
                            context = context,
                            newName = username,
                            newBio = bio,
                            newAddress = address,
                            newPhoneNumber = phoneNumber,
                            newProfilePicUri = profilePicUri,
                            newBannerUri = bannerUri,
                            removeProfilePic = removeProfilePic,
                            removeBanner = removeBanner
                        )
                    }
                },
                icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
                text = { Text("Save") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                expanded = !isLoading,
                modifier = Modifier.padding(bottom = 25.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Foto profil dan Username
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- KODE GABUNGAN UNTUK FOTO PROFIL & TOMBOL HAPUS ---
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .clickable { profileImagePicker.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = if (removeProfilePic) R.drawable.profile else (profilePicUri ?: userProfile?.profilePictureUrl),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.profile)
                            )
                        }
                        TextButton(onClick = {
                            removeProfilePic = true
                            profilePicUri = null
                        }) {
                            Text("Remove Picture")
                        }
                    }

                    // Kolom untuk TextField Username dan supporting text
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Username must be between * to * characters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                // --- KODE GABUNGAN UNTUK BANNER & TOMBOL HAPUS ---
                Box(contentAlignment = Alignment.TopEnd) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                            .clickable { bannerImagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = if (removeBanner) null else (bannerUri ?: userProfile?.bannerUrl),
                            contentDescription = "Banner Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Text("Change Banner", color = Color.White, textAlign = TextAlign.Center)
                    }
                    IconButton(onClick = {
                        removeBanner = true
                        bannerUri = null
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove Banner", tint = Color.White)
                    }
                }

                // Form Fields lainnya
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}