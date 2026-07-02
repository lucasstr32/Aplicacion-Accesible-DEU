package com.sindicato.aplicacionaccesible.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.Template
import com.sindicato.aplicacionaccesible.ui.theme.AppTheme

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sindicato.aplicacionaccesible.data.repository.ButtonRepository
import com.sindicato.aplicacionaccesible.data.repository.TemplateRepository
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffectButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.TTSButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SoundboardViewModel(
    val templateRepository: TemplateRepository,
    val buttonRepository: ButtonRepository
): ViewModel() {

    private val _templates = mutableStateListOf<Template>()
    val templates: List<Template> = _templates
    var currentTemplateIndex by mutableIntStateOf(0)
    var isEditMode by mutableStateOf(false)
    var columnCount by mutableIntStateOf(2)

    // Configuración de la aplicación (Global)
    //var appTheme by mutableStateOf(AppTheme.LIGHT)
//    var appLanguage by mutableStateOf("Español") // "Español", "Inglés"
//    var ttsPitch by mutableFloatStateOf(1.0f)
//    var ttsSpeed by mutableFloatStateOf(1.0f)

//    private var mediaPlayer: MediaPlayer? = null
//    //private var tts: TextToSpeech? = null
//    private var isTtsReady = false

    private val _appTheme = MutableStateFlow(AppTheme.LIGHT)
    val appTheme: StateFlow<AppTheme> = _appTheme.asStateFlow()




    init {
        loadTemplatesFromDb()
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
        val template = Template(name = name)
        _templates.add(template)
        currentTemplateIndex = _templates.size - 1
        isEditMode = true // Ponemos directamente en edit mode

        viewModelScope.launch {
            templateRepository.insertTemplate(template)

        }
    }


    private fun loadTemplatesFromDb() {
        viewModelScope.launch {
            try {
                val dbTemplates = templateRepository.getAllTemplates()
                _templates.clear()
                _templates.addAll(dbTemplates)

                // Log to verify info is arriving
                Log.d("Templates", "Loaded ${dbTemplates.size} templates from DB")
            } catch (e: Exception) {
                Log.e("Templates", "Error loading templates", e)
            }
        }
    }

    fun deleteCurrentTemplate() {
        if (_templates.isNotEmpty()) {
            val templateId = _templates[currentTemplateIndex].id
            _templates.removeAt(currentTemplateIndex)

            viewModelScope.launch {
                templateRepository.deleteTemplateFromDb(templateId)
            }

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

    fun addTTSButtonToCurrentTemplate(
        name: String,
        ttsText: String,colorLong: Long,
        iconRes: Int,
        position: Int
    ) {
        val currentTemplate = _templates.getOrNull(currentTemplateIndex) ?: return
        val newButton = TTSButton(
            name = name,
            gridPosition = position,
            ttsText = ttsText,
            color = colorLong,
            iconRes = iconRes
        )
        saveButton(currentTemplate, newButton, position)
    }

    fun addSoundEffectButtonToCurrentTemplate(
        name: String,
        soundEffect: SoundEffect,
        colorLong: Long,
        iconRes: Int,
        position: Int
    ) {
        val currentTemplate = _templates.getOrNull(currentTemplateIndex) ?: return
        val newButton = SoundEffectButton(
            name = name,
            gridPosition = position,
            soundEffect = soundEffect,
            color = colorLong,
            iconRes = iconRes
        )
        saveButton(currentTemplate, newButton, position)
    }

    // Helper to avoid code duplication
    private fun saveButton(template: Template, button: SoundButton, position: Int) {
        val updatedButtons = template.buttons.filter { it.gridPosition != position } + button
        _templates[currentTemplateIndex] = template.copy(buttons = updatedButtons)
        viewModelScope.launch {
            buttonRepository.insertButton(template.id, button)
        }
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

    fun deleteButton(button: SoundButton) {
        deleteButtonAtPosition(button.gridPosition)
        viewModelScope.launch {
            buttonRepository.deleteButton(
                currentTemplateIndex.toString(),
                button.gridPosition)
        }
    }
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


    fun updateTTSButton(
        button: SoundButton,
        newName: String,
        newTts: String,
        newColor: Long,
        newIcon: Int
    ) {
        val currentTemplate = _templates.getOrNull(currentTemplateIndex) ?: return

        // Create a new TTSButton instance with updated values
        val updatedButton = TTSButton(
            name = newName,
            gridPosition = button.gridPosition,
            ttsText = newTts,
            color = newColor,
            iconRes = newIcon
        )

        // Update the list state
        val updatedButtons = currentTemplate.buttons.map {
            if (it.gridPosition == button.gridPosition) updatedButton else it
        }

        _templates[currentTemplateIndex] = currentTemplate.copy(buttons = updatedButtons)

        // Persist to DB
        viewModelScope.launch {
            buttonRepository.updateButton(currentTemplate.id, button.gridPosition, updatedButton)
        }
    }

    fun updateSoundEffectButton(
        button: SoundButton,
        newName: String,
        newEffect: SoundEffect,
        newColor: Long,
        newIcon: Int
    ) {
        val currentTemplate = _templates.getOrNull(currentTemplateIndex) ?: return

        // Create a new SoundEffectButton instance with updated values
        val updatedButton = SoundEffectButton(
            name = newName,
            gridPosition = button.gridPosition,
            soundEffect = newEffect,
            color = newColor,
            iconRes = newIcon
        )

        // Update the list state
        val updatedButtons = currentTemplate.buttons.map {
            if (it.gridPosition == button.gridPosition) updatedButton else it
        }

        _templates[currentTemplateIndex] = currentTemplate.copy(buttons = updatedButtons)

        // Persist to DB
        viewModelScope.launch {
            buttonRepository.updateButton(currentTemplate.id, button.gridPosition, updatedButton)
        }
    }

    fun setAppTheme(newTheme: AppTheme) {

        _appTheme.value = newTheme
    }




//    override fun onCleared() {
//        super.onCleared()
//        mediaPlayer?.release()
//        tts?.stop()
//        tts?.shutdown()
//    }
}

