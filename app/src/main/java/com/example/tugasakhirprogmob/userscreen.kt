@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.tugasakhirprogmob

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.ui.components.BottomNavBar
import com.example.tugasakhirprogmob.ui.components.SearchHistoryView
import com.example.tugasakhirprogmob.ui.components.TopBar
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import com.example.tugasakhirprogmob.viewmodel.Product
import com.example.tugasakhirprogmob.viewmodel.ProductViewModel
import com.example.tugasakhirprogmob.viewmodel.ProfileViewModel
import com.example.tugasakhirprogmob.viewmodel.SearchViewModel
import com.example.tugasakhirprogmob.viewmodel.UserProfile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit


@Composable
fun UserProfileScreen(
    navController: NavController,
    onCartClick: () -> Unit,
    productViewModel: ProductViewModel,
    searchViewModel: SearchViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val realProducts by productViewModel.products.collectAsStateWithLifecycle()
    val userProducts by productViewModel.userProducts.collectAsStateWithLifecycle()
    val searchUiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchBarFocused by remember { mutableStateOf(false) }
    var searchExecuted by remember { mutableStateOf(false) }

    val displayedProducts = if (searchExecuted) {
        realProducts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.brand.contains(searchQuery, ignoreCase = true)
        }
    } else {
        realProducts
    }

    fun performSearch(query: String) {
        val trimmedQuery = query.trim()
        keyboardController?.hide()
        focusManager.clearFocus()

        if (trimmedQuery.isNotBlank()) {
            searchViewModel.executeSearch(trimmedQuery, realProducts)
        }

        searchQuery = trimmedQuery
        searchExecuted = trimmedQuery.isNotBlank()
        isSearchBarFocused = false
    }

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    Scaffold(
        topBar = {
            Column (
                modifier = Modifier.statusBarsPadding()
            ) {
                // --- PERBAIKAN DI SINI ---
                TopBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        searchViewModel.onSearchQueryChanged(it)
                        if (it.isBlank()) {
                            searchExecuted = false
                        }
                    },
                    onSearch = { performSearch(it) },
                    onFocusChange = { isFocused -> isSearchBarFocused = isFocused },
                    onCartClick = onCartClick,
                    onNotificationClick = { navController.navigate("notifications") } // Menambahkan parameter yang hilang
                )
                // --- AKHIR PERBAIKAN ---
            }
        },
        bottomBar = {
            if (!isSearchBarFocused && !searchExecuted) {
                BottomNavBar(navController = navController)
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isSearchBarFocused) {
                SearchHistoryView(
                    history = searchUiState.searchHistory,
                    onHistoryClick = { historyTerm ->
                        searchQuery = historyTerm
                        performSearch(historyTerm)
                    }
                )
            } else if (searchExecuted) {
                SearchResultsUI(
                    query = searchQuery,
                    products = displayedProducts,
                    navController = navController,
                    onDismiss = {
                        focusManager.clearFocus()
                        searchQuery = ""
                        searchExecuted = false
                    }
                )
            } else {
                DefaultUserProfileContent(
                    navController = navController,
                    userProducts = userProducts,
                    productViewModel = productViewModel,
                    userProfile = userProfile,
                    isMyProfile = true
                )
            }
        }
    }
}

private fun formatJoinDate(timestamp: Timestamp?): String {
    if (timestamp == null) return "N/A"
    val diff = Timestamp.now().seconds - timestamp.seconds
    val years = TimeUnit.SECONDS.toDays(diff) / 365
    val months = (TimeUnit.SECONDS.toDays(diff) % 365) / 30
    return when {
        years > 0 -> "${years}y, ${months}mo"
        months > 0 -> "${months}mo"
        else -> "<1mo"
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultUserProfileContent(
    navController: NavController,
    userProducts: List<Product>,
    productViewModel: ProductViewModel,
    userProfile: UserProfile?,
    isMyProfile: Boolean
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Listing", "Terjual")

    val availableProducts = userProducts.filter { it.stock > 0 }
    val soldProducts = userProducts.filter { it.stock <= 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF5F7F9))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB3B3B3))
        ) {
            AsyncImage(
                model = userProfile?.bannerUrl,
                contentDescription = "User Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Column {
                Spacer(modifier = Modifier.height(15.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        ) {
                            AsyncImage(
                                model = userProfile?.profilePictureUrl,
                                contentDescription = "User Profile Picture",
                                placeholder = painterResource(id = R.drawable.profile),
                                error = painterResource(id = R.drawable.profile),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(userProfile?.name ?: "Loading...",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                            Text(userProfile?.address ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                        }

//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                Text("4,5", style = MaterialTheme.typography.bodyMedium)
//                                Spacer(modifier = Modifier.width(4.dp))
//                                Icon(
//                                    painter = painterResource(id = R.drawable.star),
//                                    contentDescription = null,
//                                    tint = Color(0xFF4C4F5E),
//                                    modifier = Modifier.size(16.dp)
//                                )
//                            }
//                            Text("3 Reviews", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
//                        }

                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // --- PERUBAHAN 2: Menggunakan fungsi formatJoinDate ---
                            Text(
                                text = formatJoinDate(userProfile?.createdAt),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Joined", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        if (isMyProfile) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(R.drawable.edit2 to "Edit", R.drawable.logout to "Logout").forEach { (iconId, desc) ->
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
                                            .clickable {
                                                when (desc) {
                                                    "Edit" -> {
                                                        navController.navigate(Screen.EditProfile.route)
                                                    }
                                                    "Logout" -> {
                                                        coroutineScope.launch {
                                                            performLogout(context)
                                                        }
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = iconId),
                                            contentDescription = desc,
                                            tint = Color.Black,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("About", style = MaterialTheme.typography.titleMedium)
                        Text(userProfile?.bio ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clickable { navController.navigate("order_history") },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.history),
                    contentDescription = "Riwayat Pesanan",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Riwayat Pesanan", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.kanan),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                when (selectedTabIndex) {
                    0 -> ProductGrid(
                        navController = navController,
                        products = availableProducts,
                        showAddCard = isMyProfile,
                        onDeleteClick = { product ->
                            productToDelete = product
                            showDeleteDialog = true
                        }
                    )
                    1 -> ProductGrid(
                        navController = navController,
                        products = soldProducts,
                        showAddCard = false,
                        onDeleteClick = { product ->
                            productToDelete = product
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog && productToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Produk") },
            text = { Text("Anda yakin ingin menghapus '${productToDelete?.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        productToDelete?.let { productViewModel.deleteProduct(it.id) }
                        showDeleteDialog = false
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductGrid(
    navController: NavController,
    products: List<Product>,
    showAddCard: Boolean,
    onDeleteClick: (Product) -> Unit
) {
    val addCardText = if (products.isEmpty() && showAddCard) "Mulai Berjualan" else "Tambah Produk"

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .padding(16.dp)
            .heightIn(max = 1000.dp),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(products, key = { it.id }) { product ->
            UserProductCard(
                product = product,
                onClick = { navController.navigate("productDetail/${product.id}") },
                onEditClick = { navController.navigate("add_product_edit/${product.id}") },
                onDeleteClick = { onDeleteClick(product) }
            )
        }
        if (showAddCard) {
            item {
                AddProductCard(text = addCardText) { navController.navigate(Screen.Add.route) }
            }
        }
    }
}


@Composable
fun UserProductCard(
    product: Product,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    val isSoldOut = product.stock <= 0

    Card(
        modifier = Modifier.clickable(enabled = !isSoldOut, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull() ?: product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (isSoldOut) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SOLD OUT", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .height(88.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = product.brand,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    if (!isSoldOut) { // Sembunyikan menu jika sudah terjual
                        Box {
                            IconButton(
                                onClick = { menuExpanded = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(text = { Text("Edit") }, onClick = {
                                    menuExpanded = false
                                    onEditClick()
                                })
                                DropdownMenuItem(text = { Text("Hapus") }, onClick = {
                                    menuExpanded = false
                                    onDeleteClick()
                                })
                            }
                        }
                    }
                }
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatCurrency.format(product.price),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AddProductCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE9E9E9)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product",
                    modifier = Modifier.size(32.dp),
                    tint = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    val fakeSearchViewModel = object : SearchViewModel() {}
    val fakeProductViewModel = ProductViewModel()
    val fakeProfileViewModel = ProfileViewModel()

    TugasAkhirProgmobTheme {
        UserProfileScreen(
            navController = rememberNavController(),
            onCartClick = {},
            productViewModel = fakeProductViewModel,
            searchViewModel = fakeSearchViewModel,
            profileViewModel = fakeProfileViewModel
        )
    }
}

fun performLogout(context: Context) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    googleSignInClient.signOut().addOnCompleteListener {
        val intent = Intent(context, Register::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}