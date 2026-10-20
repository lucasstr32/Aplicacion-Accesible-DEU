package com.sindicato.aplicacionaccesible.ui.sound

import java.util.Locale


/**
 * @param requiresCountry si es true, sólo se ofrece cuando el motor TTS tiene voces
 * para esa variante regional (no alcanza con el idioma genérico).
 * @param sampleText frase para escuchar una voz de prueba.
 */
enum class AppLanguage(
    val displayName: String,
    val locale: Locale,
    val sampleText: String,
    val requiresCountry: Boolean = false
) {
    SPANISH("Español (España)", Locale("es", "ES"), "Hola, así suena esta voz."),
    SPANISH_AR("Español (Argentina)", Locale("es", "AR"), "Hola, así suena esta voz.", requiresCountry = true),
    ENGLISH("English", Locale.US, "Hello, this is how this voice sounds.");

    // Add more here easily:
    // PORTUGUESE("Português", Locale("pt", "BR"), "Olá, esta é a voz.")
}
