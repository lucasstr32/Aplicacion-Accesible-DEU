package com.sindicato.aplicacionaccesible.ui.sound

import java.util.Locale


enum class AppLanguage(val displayName: String, val locale: Locale) {
    SPANISH("Español", Locale("es", "ES")),
    ENGLISH("English", Locale.US);

    // Add more here easily:
    // PORTUGUESE("Português", Locale("pt", "BR"))
}