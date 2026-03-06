package com.hooked.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HookedColorScheme = darkColorScheme(
    primary = Color(0xFFFFD54F),
    secondary = Color(0xFF4FC3F7),
    background = Color(0xFF0E1020),
    surface = Color(0xFF1A1D2F)
)

@Composable
fun HookedTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = HookedColorScheme, content = content)
}
