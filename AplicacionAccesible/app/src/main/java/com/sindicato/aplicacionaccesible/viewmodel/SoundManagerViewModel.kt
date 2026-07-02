package com.sindicato.aplicacionaccesible.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.ViewModel
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffectButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.TTSButton
import com.sindicato.aplicacionaccesible.ui.sound.AppLanguage
import com.sindicato.aplicacionaccesible.ui.sound.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SoundManagerViewModel(): ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    //private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null

    private val _ttsPitch = MutableStateFlow(1.0f)
    val ttsPitch: StateFlow<Float> = _ttsPitch.asStateFlow()
    private val _ttsSpeechRate = MutableStateFlow(1.0f)
    val ttsSpeechRate: StateFlow<Float> = _ttsSpeechRate.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.SPANISH)
    val selectedLanguage = _selectedLanguage.asStateFlow()


    fun playSound(context: Context, button: SoundButton) {
        when (button) {
            is TTSButton -> {
                // Smart cast to TTSButton: access ttsText safely
                if (button.ttsText?.isNotBlank() == true) {
                    Log.d("SoundManager", "Playing TTS: ${button.ttsText}")
                    TTSManager.speak(button.ttsText)
                }
            }
            is SoundEffectButton -> {
                // Smart cast to SoundEffectButton: access soundEffect safely
                Log.d("SoundManager", "Playing Sound Effect: ${button.soundEffect?.name}")
                stopAndReleasePlayer()
                mediaPlayer = MediaPlayer.create(context, button.soundEffect.resourceId)
                mediaPlayer?.start()
            }
        }
    }


    fun previewSound(context: Context, soundEffect: SoundEffect) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, soundEffect.resourceId)
        mediaPlayer?.start()
    }


    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        speechRecognizer?.destroy()
        super.onCleared()
    }


    fun updateLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        // Apply directly to the audio engine
        TTSManager.applyLanguage(language)
    }

//    fun getSpeechLanguage(): String {
//        return TTSManager.language.toString()
//    }

    fun setSpeechRate(speed: Float){
        TTSManager.setSpeechRate(speed)
        _ttsSpeechRate.value = speed
    }


    fun setPitch(pitch: Float){
        TTSManager.setPitch(pitch)
        _ttsPitch.value = pitch
    }

    private fun stopAndReleasePlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}