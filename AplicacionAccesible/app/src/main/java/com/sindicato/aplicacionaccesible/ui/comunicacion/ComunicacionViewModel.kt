package com.sindicato.aplicacionaccesible.ui.comunicacion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sindicato.aplicacionaccesible.data.AppDatabase
import com.sindicato.aplicacionaccesible.data.PhraseEntity
import com.sindicato.aplicacionaccesible.ui.sound.TTSManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Indica si [text] ya está entre [phrases], sin distinguir mayúsculas ni
 * espacios en los extremos.
 */
fun isDuplicatePhrase(text: String, phrases: List<PhraseEntity>): Boolean {
    val normalized = text.trim()
    return phrases.any { it.text.trim().equals(normalized, ignoreCase = true) }
}

class ComunicacionViewModel(context: Context) : ViewModel(), RecognitionListener {

    // Se usa el contexto de la aplicación para no retener la Activity
    private val context: Context = context.applicationContext

    private val phraseDao = AppDatabase.getDatabase(context).phraseDao()
    private val _uiState = MutableStateFlow(ComunicacionUiState())
    val uiState: StateFlow<ComunicacionUiState> = _uiState.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null

    init {
        setupSpeechRecognizer()
        observePhrases()
        observeTtsProgress()
    }

    private fun observeTtsProgress() {
        viewModelScope.launch {
            TTSManager.isSpeaking.collect { speaking ->
                _uiState.update {
                    it.copy(ttsStatus = if (speaking) TtsStatus.REPRODUCIENDO else TtsStatus.IDLE)
                }
            }
        }
    }

    private fun observePhrases() {
        viewModelScope.launch {
            phraseDao.getAllPhrases().collect { phrases ->
                _uiState.update { it.copy(savedPhrases = phrases) }
            }
        }
    }

    /**
     * Guarda el texto actual como frase rápida.
     * Devuelve false si ya existía una frase igual.
     */
    fun saveCurrentTtsAsPhrase(): Boolean {
        val text = _uiState.value.ttsText.trim()
        if (text.isBlank()) return false
        if (isDuplicatePhrase(text, _uiState.value.savedPhrases)) return false
        viewModelScope.launch {
            phraseDao.insertPhrase(PhraseEntity(text = text))
        }
        return true
    }

    fun deletePhrase(phrase: PhraseEntity) {
        viewModelScope.launch {
            phraseDao.deletePhrase(phrase)
        }
    }

    private fun setupSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
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

    fun onSttTextChanged(text: String) {
        _uiState.update { it.copy(sttText = text) }
    }

    fun speak() {
        val text = _uiState.value.ttsText
        if (text.isNotBlank()) {
            TTSManager.speak(text)
        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, TTSManager.currentLanguage.locale.toLanguageTag())
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
        speechRecognizer?.destroy()
        speechRecognizer = null
        super.onCleared()
    }
}
