package com.sindicato.aplicacionaccesible.ui.screens.soundgrid

import java.util.UUID

data class SoundButton(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconRes: Int,
    val color: Long,
    val soundUri: String, // mapeo con sonido
    val gridPosition: Int // para drag and drop
)
