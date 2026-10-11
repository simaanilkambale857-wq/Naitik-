package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// NK Social 2.0 Brand Colors (Blue, Purple, Neon Cyan, White)
val NkBlue = Color(0xFF2563EB)
val NkBlueLight = Color(0xFF60A5FA)
val NkPurple = Color(0xFF7C3AED)
val NkPurpleLight = Color(0xFFA78BFA)
val NkCyan = Color(0xFF06B6D4)
val NkPink = Color(0xFFEC4899)
val NkViolet = Color(0xFF4F46E5)

// Dark Theme Palette
val NkDarkBackground = Color(0xFF0A0E1A)
val NkDarkSurface = Color(0xFF13192B)
val NkDarkSurfaceVariant = Color(0xFF1E263D)
val NkDarkBorder = Color(0xFF283454)
val NkDarkTextPrimary = Color(0xFFF8FAFC)
val NkDarkTextSecondary = Color(0xFF94A3B8)

// Light Theme Palette
val NkLightBackground = Color(0xFFF8FAFC)
val NkLightSurface = Color(0xFFFFFFFF)
val NkLightSurfaceVariant = Color(0xFFEEF2F6)
val NkLightBorder = Color(0xFFE2E8F0)
val NkLightTextPrimary = Color(0xFF0F172A)
val NkLightTextSecondary = Color(0xFF64748B)

// Brand Gradients
val NkBrandGradient = Brush.horizontalGradient(
    colors = listOf(NkBlue, NkPurple, NkPink)
)

val NkStoryRingGradient = Brush.sweepGradient(
    colors = listOf(NkBlue, NkCyan, NkPurple, NkPink, NkBlue)
)

val NkCardGradient = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color(0xCC000000))
)
