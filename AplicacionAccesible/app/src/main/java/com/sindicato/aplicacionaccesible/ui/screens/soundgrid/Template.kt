package com.sindicato.aplicacionaccesible.ui.screens.soundgrid

import java.util.UUID

data class Template(
    val id: String = UUID.randomUUID().toString(), // Generates something like "550e8400-e29b..."
    val name: String,
    val buttons: List<SoundButton> = emptyList() // listado de botones
)
