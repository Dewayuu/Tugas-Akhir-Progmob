package com.example.tugasakhirprogmob

sealed class Screen(val route: String) {
    object Home : Screen("home")
//    object SearchScreen : Screen("search?query={query}") {
//        fun createRoute(query: String) = "search?query=$query"
//    }
    object SearchScreen : Screen("search")
    object Add : Screen("add")
    object Profile : Screen("profile")
    object Cart : Screen("cart")

}