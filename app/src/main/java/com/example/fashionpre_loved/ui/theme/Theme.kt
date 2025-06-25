package com.example.fashionpreloved.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    // Bisa dikustom sesuai kebutuhan
)

private val DarkColors = darkColorScheme(
    // Bisa dikustom sesuai kebutuhan
)

@Composable
fun FashionPrelovedTheme(
    darkTheme: Boolean = false, // atau gunakan isSystemInDarkTheme()
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
