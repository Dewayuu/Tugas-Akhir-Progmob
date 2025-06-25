package com.example.tugasakhirprogmob.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.tugasakhirprogmob.R
import com.example.tugasakhirprogmob.Screen
import com.example.tugasakhirprogmob.viewmodel.Product
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search Edge...") },
            modifier = Modifier.weight(1f).height(56.dp).onFocusChanged { onFocusChange(it.isFocused) },
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(painter = painterResource(id = R.drawable.search), contentDescription = null, modifier = Modifier.size(26.dp)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color.White,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onCartClick) {
            Icon(painter = painterResource(id = R.drawable.cart), contentDescription = "Cart", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(Screen.Home, Screen.SearchScreen, Screen.Add, Screen.Profile)
    val icons = mapOf(
        Screen.Home to Pair(R.drawable.home_outline, R.drawable.home_filled),
        Screen.SearchScreen to Pair(R.drawable.search2, R.drawable.search_filled),
        Screen.Add to Pair(R.drawable.add, R.drawable.add_filled),
        Screen.Profile to Pair(R.drawable.profile, R.drawable.profile_filled)
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val iconPair = icons[screen] ?: return@forEach

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = if (isSelected) iconPair.second else iconPair.first),
                        contentDescription = screen.route,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }
    }
}

@Composable
fun ProductCard(product: Product, navController: NavController) {
    val formatCurrency = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    Column(modifier = Modifier.clickable { /* navController.navigate("productDetail/${product.id}") */ }) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)).background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(product.brand, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(product.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1)
        Text(formatCurrency.format(product.price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SearchHistoryView(history: List<String>, onHistoryClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
        item {
            Text(
                text = "Riwayat Pencarian",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(history) { term ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onHistoryClick(term) }.padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.History, contentDescription = "History Icon", tint = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = term, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun SearchResultsHeader(query: String) {
    val headerText = if (query.isBlank()) "Semua Produk" else "Hasil untuk \"$query\""
    Text(
        text = headerText,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}