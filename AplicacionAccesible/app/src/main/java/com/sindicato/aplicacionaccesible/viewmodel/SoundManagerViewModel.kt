package com.sindicato.aplicacionaccesible.viewmodel

import android.content.Context
import android.media.MediaPlayer
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.ViewModel
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.ui.sound.TTSManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

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



    fun playSound(context: Context, button: SoundButton) {
        if (button.ttsText != null && button.ttsText != "") {
            Log.d("TTSManager", "Reproduciendo ${button.soundEffect} ${button.ttsText}")

//            initTts(context)
//            if (isTtsReady) {
//                updateTtsSettings()
//                tts?.speak(button.ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
//            }
            TTSManager.speak(button.ttsText)
        } else if (button.soundEffect != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, button.soundEffect.resourceId)
            mediaPlayer?.start()
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


    fun updateSpeechLanguage(language: String) {
        val locale = if (language == "Español") Locale("es", "ES") else Locale.ENGLISH
        TTSManager.language = locale
    }

    fun getSpeechLanguage(): String {
        return TTSManager.language.toString()
    }

    fun setSpeechRate(speed: Float){
        TTSManager.setSpeechRate(speed)
        _ttsSpeechRate.value = speed
    }


    fun setPitch(pitch: Float){
        TTSManager.setPitch(pitch)
        _ttsPitch.value = pitch
    }

}