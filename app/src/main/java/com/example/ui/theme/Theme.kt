package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NkPurpleLight,
    onPrimary = Color.White,
    primaryContainer = NkPurple,
    onPrimaryContainer = Color.White,
    secondary = NkBlueLight,
    onSecondary = Color.White,
    secondaryContainer = NkBlue,
    onSecondaryContainer = Color.White,
    tertiary = NkCyan,
    background = NkDarkBackground,
    onBackground = NkDarkTextPrimary,
    surface = NkDarkSurface,
    onSurface = NkDarkTextPrimary,
    surfaceVariant = NkDarkSurfaceVariant,
    onSurfaceVariant = NkDarkTextSecondary,
    outline = NkDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = NkPurple,
    onPrimary = Color.White,
    primaryContainer = NkPurpleLight,
    onPrimaryContainer = Color(0xFF1E1035),
    secondary = NkBlue,
    onSecondary = Color.White,
    secondaryContainer = NkBlueLight,
    onSecondaryContainer = Color(0xFF0F2552),
    tertiary = NkCyan,
    background = NkLightBackground,
    onBackground = NkLightTextPrimary,
    surface = NkLightSurface,
    onSurface = NkLightTextPrimary,
    surfaceVariant = NkLightSurfaceVariant,
    onSurfaceVariant = NkLightTextSecondary,
    outline = NkLightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
