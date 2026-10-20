package com.sindicato.aplicacionaccesible.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sindicato.aplicacionaccesible.data.settings.SettingsRepository
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffectButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.TTSButton
import com.sindicato.aplicacionaccesible.ui.sound.AppLanguage
import com.sindicato.aplicacionaccesible.ui.sound.SoundEffectManager
import com.sindicato.aplicacionaccesible.ui.sound.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SoundManagerViewModel(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private var mediaPlayer: MediaPlayer? = null

    private val _ttsPitch = MutableStateFlow(1.0f)
    val ttsPitch: StateFlow<Float> = _ttsPitch.asStateFlow()
    private val _ttsSpeechRate = MutableStateFlow(1.0f)
    val ttsSpeechRate: StateFlow<Float> = _ttsSpeechRate.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.SPANISH)
    val selectedLanguage = _selectedLanguage.asStateFlow()

    // null = voz predeterminada del idioma
    private val _selectedVoice = MutableStateFlow<String?>(null)
    val selectedVoice: StateFlow<String?> = _selectedVoice.asStateFlow()

    init {
        // Aplica los ajustes guardados; TTSManager los conserva hasta que el motor esté listo
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _ttsPitch.value = settings.pitch
                _ttsSpeechRate.value = settings.speechRate
                _selectedLanguage.value = settings.language
                _selectedVoice.value = settings.voiceName
                TTSManager.setPitch(settings.pitch)
                TTSManager.setSpeechRate(settings.speechRate)
                TTSManager.applyLanguage(settings.language)
                TTSManager.setVoice(settings.voiceName)
            }
        }
    }

    fun playSound(context: Context, button: SoundButton) {
        when (button) {
            is TTSButton -> {
                // Smart cast to TTSButton: access ttsText safely
                if (button.ttsText.isNotBlank()) {
                    Log.d("SoundManager", "Playing TTS: ${button.ttsText}")
                    TTSManager.speak(button.ttsText)
                }
            }
            is SoundEffectButton -> {
                // Smart cast to SoundEffectButton: access soundEffect safely
                Log.d("SoundManager", "Playing Sound Effect: ${button.soundEffect.name}")
                playEffect(context, button.soundEffect)
            }
        }
    }

    fun previewSound(context: Context, soundEffect: SoundEffect) {
        playEffect(context, soundEffect)
    }

    /**
     * Reproduce con SoundPool (sin demora). Si el sonido aún no terminó de cargarse,
     * usa MediaPlayer como respaldo para que el toque nunca quede en silencio.
     */
    private fun playEffect(context: Context, soundEffect: SoundEffect) {
        stopAndReleasePlayer()
        if (!SoundEffectManager.playSound(soundEffect.resourceId)) {
            mediaPlayer = MediaPlayer.create(context, soundEffect.resourceId)
            mediaPlayer?.start()
        }
    }

    override fun onCleared() {
        stopAndReleasePlayer()
        super.onCleared()
    }

    fun updateLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        // Cada voz pertenece a un idioma: al cambiarlo se vuelve a la predeterminada
        _selectedVoice.value = null
        TTSManager.setVoice(null)
        TTSManager.applyLanguage(language)
        viewModelScope.launch {
            settingsRepository.setVoice(null)
            settingsRepository.setLanguage(language)
        }
    }

    fun selectVoice(name: String?) {
        _selectedVoice.value = name
        TTSManager.setVoice(name)
        viewModelScope.launch { settingsRepository.setVoice(name) }
    }

    /**
     * Elige la voz y dice una frase de prueba en el idioma actual.
     */
    fun previewVoice(name: String?) {
        selectVoice(name)
        TTSManager.speak(_selectedLanguage.value.sampleText)
    }

    fun setSpeechRate(speed: Float){
        TTSManager.setSpeechRate(speed)
        _ttsSpeechRate.value = speed
        viewModelScope.launch { settingsRepository.setSpeechRate(speed) }
    }

    fun setPitch(pitch: Float){
        TTSManager.setPitch(pitch)
        _ttsPitch.value = pitch
        viewModelScope.launch { settingsRepository.setPitch(pitch) }
    }

    private fun stopAndReleasePlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}