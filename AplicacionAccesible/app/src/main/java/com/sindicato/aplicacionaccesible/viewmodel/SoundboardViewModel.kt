package com.sindicato.aplicacionaccesible.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.Template
import com.sindicato.aplicacionaccesible.ui.theme.AppTheme

import android.speech.tts.TextToSpeech
import android.util.Log
import com.sindicato.aplicacionaccesible.ui.sound.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class SoundboardViewModel: ViewModel() {

    private val _templates = mutableStateListOf<Template>()
    val templates: List<Template> = _templates
    var currentTemplateIndex by mutableIntStateOf(0)
    var isEditMode by mutableStateOf(false)
    var columnCount by mutableIntStateOf(2)

    // Configuración de la aplicación (Global)
    var appTheme by mutableStateOf(AppTheme.LIGHT)
//    var appLanguage by mutableStateOf("Español") // "Español", "Inglés"
//    var ttsPitch by mutableFloatStateOf(1.0f)
//    var ttsSpeed by mutableFloatStateOf(1.0f)

//    private var mediaPlayer: MediaPlayer? = null
//    //private var tts: TextToSpeech? = null
//    private var isTtsReady = false




    init {
        _templates.add(
            Template(
                "Default", listOf()

            )
        )
    }

//    private fun initTts(context: Context) {
//        if (tts == null) {
//            tts = TextToSpeech(context.applicationContext) { status ->
//                if (status == TextToSpeech.SUCCESS) {
//                    updateTtsSettings()
//                    isTtsReady = true
//                }
//            }
//        } else if (isTtsReady) {
//            updateTtsSettings()
//        }
//    }



    fun addTemplate(name: String) {
        _templates.add(Template(name = name))
        currentTemplateIndex = _templates.size - 1
        isEditMode = true // Ponemos directamente en edit mode



    }

    fun deleteCurrentTemplate() {
        if (_templates.isNotEmpty()) {
            _templates.removeAt(currentTemplateIndex)
            if (currentTemplateIndex >= _templates.size) {
                currentTemplateIndex = _templates.size - 1
            }
            if (currentTemplateIndex < 0 && _templates.isNotEmpty()) {
                currentTemplateIndex = 0
            }
        }
    }

    fun toggleEditMode() {
        isEditMode = !isEditMode
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addButtonToCurrentTemplate(
        name: String,
        soundEffect: SoundEffect?,
        ttsText: String?,
        colorLong: Long,
        iconRes: Int,
        position: Int
    ) {
        val currentTemplate = _templates.getOrNull(currentTemplateIndex) ?: return
        val updatedButtons = currentTemplate.buttons.filter { it.gridPosition != position } +
                SoundButton(name, position, soundEffect, ttsText, colorLong, iconRes)

        _templates[currentTemplateIndex] = currentTemplate.copy(buttons = updatedButtons)
    }

//    fun playSound(context: Context, button: SoundButton) {
//        if (button.ttsText != null) {
//            Log.d("TTSManager", "Reproduciendo ${button.ttsText}")
//
////            initTts(context)
////            if (isTtsReady) {
////                updateTtsSettings()
////                tts?.speak(button.ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
////            }
//            TTSManager.speak(button.ttsText)
//        } else if (button.soundEffect != null) {
//            mediaPlayer?.stop()
//            mediaPlayer?.release()
//            mediaPlayer = MediaPlayer.create(context, button.soundEffect.resourceId)
//            mediaPlayer?.start()
//        }
//    }

    fun deleteButtonAtPosition(position: Int) {
        val currentTemplate = _templates.getOrNull(currentTemplateIndex) ?: return
        val updatedButtons = currentTemplate.buttons.filter { it.gridPosition != position }
        _templates[currentTemplateIndex] = currentTemplate.copy(buttons = updatedButtons)
    }

//    fun previewSound(context: Context, soundEffect: SoundEffect) {
//        mediaPlayer?.stop()
//        mediaPlayer?.release()
//        mediaPlayer = MediaPlayer.create(context, soundEffect.resourceId)
//        mediaPlayer?.start()
//    }






//    override fun onCleared() {
//        super.onCleared()
//        mediaPlayer?.release()
//        tts?.stop()
//        tts?.shutdown()
//    }
}
