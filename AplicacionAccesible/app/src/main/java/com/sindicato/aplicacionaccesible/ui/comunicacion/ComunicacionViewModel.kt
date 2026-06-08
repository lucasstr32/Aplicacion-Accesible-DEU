package com.sindicato.aplicacionaccesible.ui.comunicacion

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sindicato.aplicacionaccesible.data.AppDatabase
import com.sindicato.aplicacionaccesible.data.PhraseEntity
import com.sindicato.aplicacionaccesible.ui.sound.TTSManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ComunicacionViewModel(var context: Context) : ViewModel(), RecognitionListener {

    private val phraseDao = AppDatabase.getDatabase(context).phraseDao()
    private val _uiState = MutableStateFlow(ComunicacionUiState())
    val uiState: StateFlow<ComunicacionUiState> = _uiState.asStateFlow()

    //private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null



    init {
        //setupTts()
        setupSpeechRecognizer()
        observePhrases()
    }

    private fun observePhrases() {
        viewModelScope.launch {
            phraseDao.getAllPhrases().collect { phrases ->
                _uiState.update { it.copy(savedPhrases = phrases) }
            }
        }
    }

    fun saveCurrentTtsAsPhrase() {
        val text = _uiState.value.ttsText
        if (text.isNotBlank()) {
            viewModelScope.launch {
                phraseDao.insertPhrase(PhraseEntity(text = text))
            }
        }
    }

    fun deletePhrase(phrase: PhraseEntity) {
        viewModelScope.launch {
            phraseDao.deletePhrase(phrase)
        }
    }

//    private fun setupTts() {
//        _uiState.update { it.copy(ttsStatus = TtsStatus.CARGANDO) }
//        tts = TextToSpeech(context) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                TTSManager.language = Locale.getDefault()
//                TTSManager.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
//                    override fun onStart(utteranceId: String?) {
//                        _uiState.update { it.copy(ttsStatus = TtsStatus.REPRODUCIENDO) }
//                    }
//
//                    override fun onDone(utteranceId: String?) {
//                        _uiState.update { it.copy(ttsStatus = TtsStatus.IDLE) }
//                    }
//
//                    @Deprecated("Deprecated in Java")
//                    override fun onError(utteranceId: String?) {
//                        _uiState.update { it.copy(ttsStatus = TtsStatus.ERROR, errorMessage = "Error en TTS") }
//                    }
//
//                    override fun onError(utteranceId: String?, errorCode: Int) {
//                        _uiState.update { it.copy(ttsStatus = TtsStatus.ERROR, errorMessage = "Error en TTS: $errorCode") }
//                    }
//                })
//                _uiState.update { it.copy(ttsStatus = TtsStatus.IDLE) }
//            } else {
//                _uiState.update { it.copy(ttsStatus = TtsStatus.ERROR, errorMessage = "No se pudo inicializar TTS") }
//            }
//        }
//    }

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

//    override fun onCleared() {
//        tts?.stop()
//        tts?.shutdown()
//        speechRecognizer?.destroy()
//        super.onCleared()
//    }
//
//
//    fun updateLanguage(language: String) {
//        val locale = if (language == "Español") Locale("es", "ES") else Locale.ENGLISH
//        TTSManager.language = locale
//    }
//
//    fun getLanguage(): String {
//        return TTSManager.language.toString()
//    }
//
//    fun setSpeechRate(speed: Float){
//        TTSManager.setSpeechRate(speed)
//        _ttsSpeechRate.value = speed
//    }
//
//
//    fun setPitch(pitch: Float){
//        TTSManager.setPitch(pitch)
//        _ttsPitch.value = pitch
//    }
}
