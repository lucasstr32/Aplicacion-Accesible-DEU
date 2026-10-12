package com.sindicato.aplicacionaccesible.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Standard Light
val LightPrimary = Color(0xFF0061A4)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightSecondary = Color(0xFF535F70)
val LightOnSecondary = Color(0xFFFFFFFF)

// Standard Dark
val DarkPrimary = Color(0xFF9ECAFF)
val DarkOnPrimary = Color(0xFF003258)
val DarkSecondary = Color(0xFFBBC7DB)
val DarkOnSecondary = Color(0xFF253140)

// Colorblind (High Contrast - Blue/Yellow)
val ColorblindPrimary = Color(0xFF004488) // Deep Blue
val ColorblindOnPrimary = Color(0xFFFFFFFF)
val ColorblindSecondary = Color(0xFFEEEE00) // Bright Yellow
val ColorblindOnSecondary = Color(0xFF000000)
val ColorblindBackground = Color(0xFFFFFFFF)
val ColorblindSurface = Color(0xFFFFFFFF)
val ColorblindOnBackground = Color(0xFF000000)
val ColorblindOnSurface = Color(0xFF000000)

// Colorblind-safe palette (High contrast and distinct hues)
val SafeRed = Color(0xFFD55E00)    // Vermillion
val SafeBlue = Color(0xFF0072B2)   // Blue
val SafeYellow = Color(0xFFF0E442) // Yellow
val SafeGreen = Color(0xFF009E73)  // Bluish Green
val SafeOrange = Color(0xFFE69F00) // Orange
val SafePurple = Color(0xFFCC79A7) // Reddish Purple

val SafeColors = listOf(SafeRed, SafeBlue, SafeYellow, SafeGreen, SafeOrange, SafePurple)


/**
 * Luminancia relativa según WCAG 2.x (componentes sRGB linealizados).
 */
fun relativeLuminance(color: Color): Double {
    fun channel(c: Float): Double {
        val v = c.toDouble()
        return if (v <= 0.03928) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
    }
    return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
}

/**
 * Relación de contraste WCAG entre dos colores (de 1:1 a 21:1).
 */
fun contrastRatio(first: Color, second: Color): Double {
    val l1 = relativeLuminance(first)
    val l2 = relativeLuminance(second)
    return (maxOf(l1, l2) + 0.05) / (minOf(l1, l2) + 0.05)
}

/**
 * Devuelve negro o blanco, el que tenga mayor contraste con el fondo.
 */
fun getContrastColor(backgroundColor: Color): Color {
    val withBlack = contrastRatio(backgroundColor, Color.Black)
    val withWhite = contrastRatio(backgroundColor, Color.White)
    return if (withBlack >= withWhite) Color.Black else Color.White
}
