package com.sindicato.aplicacionaccesible.ui.screens.soundgrid

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SoundboardViewModel {

    private val _templates = mutableStateListOf<Template>()
    val templates: List<Template> = _templates

    var currentTemplateIndex by mutableIntStateOf(0)
    var isEditMode by mutableStateOf(false)
    var columnCount by mutableIntStateOf(2)

    private var mediaPlayer: MediaPlayer? = null

    init {
        _templates.add(Template("Default", listOf(
            SoundButton("Bomb", 0, SoundEffect.BOMB),
            SoundButton("Kiss", 1, SoundEffect.KISS)
        )))
    }

    fun addTemplate(name: String) {
        _templates.add(Template(name = name))
        currentTemplateIndex = _templates.size - 1
    }

    fun toggleEditMode() {
        isEditMode = !isEditMode
    }

    fun addButtonToCurrentTemplate(name: String, soundEffect: SoundEffect, position: Int) {
        val currentTemplate = _templates.getOrNull(currentTemplateIndex) ?: return
        val updatedButtons = currentTemplate.buttons.filter { it.gridPosition != position } + 
                            SoundButton(name, position, soundEffect)
        
        _templates[currentTemplateIndex] = currentTemplate.copy(buttons = updatedButtons)
    }

    fun playSound(context: Context, soundEffect: SoundEffect) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, soundEffect.resourceId)
        mediaPlayer?.start()
    }
}