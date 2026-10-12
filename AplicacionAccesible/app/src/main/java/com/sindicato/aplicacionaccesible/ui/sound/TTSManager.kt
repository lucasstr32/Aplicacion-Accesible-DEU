package com.sindicato.aplicacionaccesible.ui.sound

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale


object TTSManager {

    private var isInitialized = false
    private var tts: TextToSpeech? = null
    var currentLanguage: AppLanguage = AppLanguage.SPANISH
        private set

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()


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
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        _isSpeaking.value = false
                    }
                })
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