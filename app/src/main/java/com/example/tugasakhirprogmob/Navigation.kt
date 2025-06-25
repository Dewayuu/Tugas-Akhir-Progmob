package com.example.tugasakhirprogmob

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Add : Screen("add")
    object Profile : Screen("profile")
}