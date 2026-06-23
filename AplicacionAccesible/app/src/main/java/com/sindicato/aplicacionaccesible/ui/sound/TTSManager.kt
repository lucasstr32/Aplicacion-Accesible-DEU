package com.sindicato.aplicacionaccesible.ui.sound

import android.content.Context
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sindicato.aplicacionaccesible.data.PhraseEntity
import com.sindicato.aplicacionaccesible.ui.comunicacion.ComunicacionMode
import com.sindicato.aplicacionaccesible.ui.comunicacion.SttStatus
import com.sindicato.aplicacionaccesible.ui.comunicacion.TtsStatus
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale


object TTSManager {

    private var isInitialized = false
    private var tts: TextToSpeech? = null
    private var currentLanguage: AppLanguage = AppLanguage.SPANISH


    val ttsText: String = ""


    fun init(context: Context) {
        Log.d("TTSManager", "Inicializando TTS. Estado: $isInitialized")
        if(isInitialized) return

        setupTts(context)
    }



    private fun setupTts(context: Context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.0f)
                applyLanguage(currentLanguage)
            }
        }
    }

    fun applyLanguage(language: AppLanguage) {
        currentLanguage = language
        val result = tts?.setLanguage(language.locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTSManager", "Language ${language.displayName} not supported")
        }
    }


    fun speak(text: String) {
        Log.d("TTSManager", "Texto a reproducir: $text")
        if (text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
        }
    }

    fun setPitch(pitch: Float){
        tts?.setPitch(pitch)
    }

    fun setSpeechLanguage(language: Locale){
        tts?.language = language
    }


    fun setSpeechRate(rate: Float){
        tts?.setSpeechRate(rate)
    }

    fun release(){
        tts?.stop()
        tts?.shutdown()
        isInitialized = false
    }



}