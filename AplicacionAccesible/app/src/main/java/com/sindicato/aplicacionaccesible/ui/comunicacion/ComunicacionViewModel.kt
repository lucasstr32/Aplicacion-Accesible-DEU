package com.sindicato.aplicacionaccesible.ui.comunicacion

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class ComunicacionViewModel(application: Application) : AndroidViewModel(application), RecognitionListener {

    private val _uiState = MutableStateFlow(ComunicacionUiState())
    val uiState: StateFlow<ComunicacionUiState> = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null

    init {
        setupTts()
        setupSpeechRecognizer()
    }

    private fun setupTts() {
        _uiState.update { it.copy(ttsStatus = TtsStatus.CARGANDO) }
        tts = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _uiState.update { it.copy(ttsStatus = TtsStatus.REPRODUCIENDO) }
                    }

                    override fun onDone(utteranceId: String?) {
                        _uiState.update { it.copy(ttsStatus = TtsStatus.IDLE) }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _uiState.update { it.copy(ttsStatus = TtsStatus.ERROR, errorMessage = "Error en TTS") }
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _uiState.update { it.copy(ttsStatus = TtsStatus.ERROR, errorMessage = "Error en TTS: $errorCode") }
                    }
                })
                _uiState.update { it.copy(ttsStatus = TtsStatus.IDLE) }
            } else {
                _uiState.update { it.copy(ttsStatus = TtsStatus.ERROR, errorMessage = "No se pudo inicializar TTS") }
            }
        }
    }

    private fun setupSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplication())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
            speechRecognizer?.setRecognitionListener(this)
        } else {
            _uiState.update { it.copy(errorMessage = "Reconocimiento de voz no disponible") }
        }
    }

    fun changeMode(mode: ComunicacionMode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun onTtsTextChanged(text: String) {
        _uiState.update { it.copy(ttsText = text) }
    }

    fun speak() {
        val text = _uiState.value.ttsText
        if (text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechRecognizer?.startListening(intent)
        _uiState.update { it.copy(sttStatus = SttStatus.ESCUCHANDO) }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    // RecognitionListener implementation
    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {
        _uiState.update { it.copy(sttStatus = SttStatus.ESCUCHANDO) }
    }
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {
        _uiState.update { it.copy(sttStatus = SttStatus.PROCESANDO) }
    }
    override fun onError(error: Int) {
        val message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
            SpeechRecognizer.ERROR_CLIENT -> "Error del cliente"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
            SpeechRecognizer.ERROR_NETWORK -> "Error de red"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Tiempo de espera de red agotado"
            SpeechRecognizer.ERROR_NO_MATCH -> "No se encontró coincidencia"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "El reconocedor está ocupado"
            SpeechRecognizer.ERROR_SERVER -> "Error del servidor"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No se detectó voz"
            else -> "Error desconocido"
        }
        _uiState.update { it.copy(sttStatus = SttStatus.ERROR, errorMessage = message) }
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            _uiState.update { it.copy(sttText = matches[0], sttStatus = SttStatus.IDLE) }
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        speechRecognizer?.destroy()
        super.onCleared()
    }
}
