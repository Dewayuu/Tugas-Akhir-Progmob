package com.example.fashionpreloved

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.fashionpreloved.ui.MainNavHost
import com.example.fashionpreloved.ui.theme.FashionPrelovedTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars

import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.res.painterResource

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ScaffoldDefaults.contentWindowInsets
import androidx.navigation.NavController
import androidx.navigation.compose.*

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Alignment




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FashionPrelovedApp() }
    }
}

@Composable
fun FashionPrelovedApp() {
    val navController = rememberNavController()

    FashionPrelovedTheme {
        Scaffold(
            contentWindowInsets = WindowInsets.navigationBars,
            bottomBar = { BottomBar(navController) }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                MainNavHost(navController)
            }
        }
    }

}

/* ---------- Bottom Navigation dengan ikon saja ---------- */
@Composable
fun BottomBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        modifier = Modifier
            .height(64.dp)
            .navigationBarsPadding()
    ) {
        bottomItem("home", R.drawable.ic_home, currentRoute, navController)
        bottomItem("explore", R.drawable.ic_explore, currentRoute, navController)
        bottomItem("create", R.drawable.ic_create, currentRoute, navController)
        bottomItem("profile", R.drawable.ic_profile, currentRoute, navController)
    }
}



@Composable
private fun RowScope.bottomItem(
    route: String,
    iconRes: Int,
    currentRoute: String?,
    navController: NavController
) {
    NavigationBarItem(
        selected = currentRoute == route,
        onClick = {
            if (currentRoute != route) {
                navController.navigate(route)
            }
        },
        icon = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = route,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        },
        alwaysShowLabel = false,
        label = {}
    )
}

@Composable
fun CategoryChips() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = 12.dp)
    ) {
        AssistChip(
            onClick = { /* TODO: aksi Filters */ },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_filter),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Unspecified
                )
            },
            label = { Text("Filters") },
            modifier = Modifier.padding(end = 8.dp)
        )

        AssistChip(
            onClick = { /* TODO: aksi filter */ },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_history),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Unspecified
                )
            },
            label = { Text("Jacket") },
            modifier = Modifier.padding(end = 8.dp)
        )

        AssistChip(
            onClick = { /* TODO: aksi filter */ },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_filter),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            label = { Text("Women's Clothing") },
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Home Screen")
    }
}

@Composable
fun ExploreScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Explore Screen")
    }
}

@Composable
fun CreateScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Create Screen")
    }
}


