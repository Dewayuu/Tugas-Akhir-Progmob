package com.example.tugasakhirprogmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tugasakhirprogmob.ui.theme.TugasAkhirProgmobTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip


class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TugasAkhirProgmobTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen()
                }
            }
        }
    }
}

data class Product(val brand: String, val name: String, val price: String, val imageRes: Int)
data class ChipData(val label: String, val imageRes: Int)
data class CategoryItem(val label: String, val imageRes: Int)


val sampleProducts = listOf(
    Product("Nike", "Product name", "$10.99", R.drawable.nike),
    Product("Uniqlo", "Product name", "$10.99", R.drawable.uniqlo),
    Product("No Brand", "Product name", "$10.99", R.drawable.nike),
    Product("Adidas", "Product name", "$10.99", R.drawable.nike)
)

@Composable
fun HomeScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar()
        LazyColumn(modifier = Modifier.weight(1f)) {
            item { FilterChips() }
            item { TopSellingBanner() }
            item { CategoryRow() }
            item { NewestProductsSection() }
        }
        BottomNavBar()
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search Edge") },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color(0xFFE0E0E0),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

        )
        IconButton(onClick = {}) {
            Image(
                painter = painterResource(id = R.drawable.cart),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun FilterChips() {
    val chips = listOf(
        ChipData("Filters", R.drawable.mage_filter),
        ChipData("Jacket", R.drawable.jacket),
        ChipData("Women's Clothing", R.drawable.t_shirt)
    )

    LazyRow(modifier = Modifier.padding(start = 16.dp)) {
        items(chips) { chip ->
            AssistChip(
                onClick = {},
                label = { Text(chip.label) },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = chip.imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun TopSellingBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(16.dp)
            .background(Color.LightGray, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Top Selling", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CategoryRow() {
    val categories = listOf(
        CategoryItem("T-Shirt", R.drawable.t_shirt),
        CategoryItem("Trousers", R.drawable.trousers),
        CategoryItem("Footwear", R.drawable.footwear),
        CategoryItem("Jacket", R.drawable.jacket)
    )

    Column(Modifier.padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Categories", fontWeight = FontWeight.Bold)
            Text(">", modifier = Modifier.align(Alignment.CenterVertically))
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(categories) { category ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .width(75.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = category.imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp) // Atur sesuai ukuran gambarmu
                        )
                    }
                    Text(category.label)
                }
            }
        }
    }
}

@Composable
fun NewestProductsSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Newest", fontWeight = FontWeight.Bold)
            Text(">")
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.height(300.dp)) {
            items(sampleProducts) { product ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(product.imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(120.dp)
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(product.brand, style = MaterialTheme.typography.labelSmall)
                    Text(product.name)
                    Text(product.price, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BottomNavBar() {
    val selectedIndex = remember { mutableStateOf(0) }

    NavigationBar {
        NavigationBarItem(
            selected = selectedIndex.value == 0,
            onClick = { selectedIndex.value = 0 },
            icon = {
                Image(
                    painter = painterResource(
                        id = if (selectedIndex.value == 0) R.drawable.home_filled
                        else R.drawable.home_outline
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedIndex.value == 1,
            onClick = { selectedIndex.value = 1 },
            icon = {
                Image(
                    painter = painterResource(
                        id = if (selectedIndex.value == 1) R.drawable.search2
                        else R.drawable.search2
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedIndex.value == 2,
            onClick = { selectedIndex.value = 2 },
            icon = {
                Image(
                    painter = painterResource(
                        id = if (selectedIndex.value == 2) R.drawable.add
                        else R.drawable.add
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedIndex.value == 3,
            onClick = { selectedIndex.value = 3 },
            icon = {
                Image(
                    painter = painterResource(
                        id = if (selectedIndex.value == 3) R.drawable.profile
                        else R.drawable.profile
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TugasAkhirProgmobTheme {
        HomeScreen()
    }
}
