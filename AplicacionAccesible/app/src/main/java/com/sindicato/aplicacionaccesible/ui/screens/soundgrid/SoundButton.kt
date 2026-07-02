package com.sindicato.aplicacionaccesible.ui.screens.soundgrid

import android.graphics.Color

sealed class SoundButton(
    open val name: String,
    open val gridPosition: Int,
    open val color: Long,
    open val iconRes: Int
)
data class SoundEffectButton(
    val soundEffect: SoundEffect,
    override val name: String,
    override val gridPosition: Int,
    override val color: Long,
    override val iconRes: Int
): SoundButton(name, gridPosition, color, iconRes)

data class TTSButton(
    val ttsText: String,
    override val name: String,
    override val gridPosition: Int,
    override val color: Long,
    override val iconRes: Int
): SoundButton(name, gridPosition, color, iconRes)