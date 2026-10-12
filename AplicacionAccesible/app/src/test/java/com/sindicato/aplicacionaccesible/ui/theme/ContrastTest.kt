package com.sindicato.aplicacionaccesible.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContrastTest {

    private val tolerance = 0.05

    private fun assertChoice(background: Color, expectedText: Color, expectedRatio: Double) {
        val text = getContrastColor(background)
        assertEquals(expectedText, text)
        assertEquals(expectedRatio, contrastRatio(background, text), tolerance)
    }

    @Test
    fun safeColorsChooseExpectedTextColor() {
        assertChoice(SafeRed, Color.Black, 5.43)
        assertChoice(SafeBlue, Color.White, 5.19)
        assertChoice(SafeYellow, Color.Black, 15.88)
        assertChoice(SafeGreen, Color.Black, 6.14)
        assertChoice(SafeOrange, Color.Black, 9.32)
        assertChoice(SafePurple, Color.Black, 6.86)
    }

    @Test
    fun allSafeColorsMeetWcagAA() {
        SafeColors.forEach { background ->
            val ratio = contrastRatio(background, getContrastColor(background))
            assertTrue("Contraste insuficiente para $background: $ratio", ratio >= 4.5)
        }
    }

    @Test
    fun blackOnWhiteIsMaximumContrast() {
        assertEquals(21.0, contrastRatio(Color.Black, Color.White), 0.01)
    }

    @Test
    fun toSafeColorKeepsPaletteAndMapsOthers() {
        SafeColors.forEach { assertEquals(it, toSafeColor(it)) }
        // Colores de la paleta anterior (buttonColors) caen en la paleta segura
        assertTrue(toSafeColor(Color(0xFFEF5350)) in SafeColors)
        assertEquals(SafeYellow, toSafeColor(Color(0xFFFFEB3B)))
    }
}
