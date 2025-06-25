package com.example.fashionpreloved.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fashionpreloved.screen.UserProfileScreen
import com.example.fashionpreloved.screen.EditProfileScreen
import com.example.fashionpreloved.screen.CartScreen
import com.example.fashionpreloved.HomeScreen
import com.example.fashionpreloved.ExploreScreen
import com.example.fashionpreloved.CreateScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("explore") { ExploreScreen(navController) }
        composable("create") { CreateScreen(navController) }
        composable("profile") { UserProfileScreen(navController) }
        composable("edit_profile") { EditProfileScreen(navController) }
        }

    }
