package com.sindicato.aplicacionaccesible.ui.sound

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


object TTSManager {

    private var isInitialized = false
    private var tts: TextToSpeech? = null

    // Valores deseados: se guardan aunque el motor no esté listo y se aplican al iniciar
    var currentLanguage: AppLanguage = AppLanguage.SPANISH
        private set
    private var pitch = 1.0f
    private var speechRate = 1.0f

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()


    fun init(context: Context) {
        Log.d("TTSManager", "Inicializando TTS. Estado: $isInitialized")
        if (isInitialized || tts != null) return

        setupTts(context.applicationContext)
    }


    private fun setupTts(context: Context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.setPitch(pitch)
                tts?.setSpeechRate(speechRate)
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
                _isReady.value = true
            } else {
                Log.e("TTSManager", "No se pudo inicializar el motor TTS: $status")
            }
        }
    }

    fun applyLanguage(language: AppLanguage) {
        currentLanguage = language
        if (!isInitialized) return
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

    fun setPitch(pitch: Float) {
        this.pitch = pitch
        if (isInitialized) tts?.setPitch(pitch)
    }

    fun setSpeechRate(rate: Float) {
        speechRate = rate
        if (isInitialized) tts?.setSpeechRate(rate)
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        _isReady.value = false
    }
}
