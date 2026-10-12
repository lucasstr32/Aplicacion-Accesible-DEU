package com.sindicato.aplicacionaccesible.ui.sound

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Voz del motor TTS: [name] es el identificador técnico y [label] el texto para mostrar.
 */
data class VoiceOption(val name: String, val label: String)


object TTSManager {

    private var isInitialized = false
    private var tts: TextToSpeech? = null

    // Valores deseados: se guardan aunque el motor no esté listo y se aplican al iniciar
    var currentLanguage: AppLanguage = AppLanguage.SPANISH
        private set
    private var pitch = 1.0f
    private var speechRate = 1.0f
    private var voiceName: String? = null

    // Idiomas que el motor puede usar; las variantes regionales sin voces se ocultan
    private val _availableLanguages = MutableStateFlow(AppLanguage.entries.filter { !it.requiresCountry })
    val availableLanguages: StateFlow<List<AppLanguage>> = _availableLanguages.asStateFlow()

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
                _availableLanguages.value = AppLanguage.entries.filter { language ->
                    val result = tts?.isLanguageAvailable(language.locale) ?: TextToSpeech.LANG_NOT_SUPPORTED
                    if (language.requiresCountry) result >= TextToSpeech.LANG_COUNTRY_AVAILABLE
                    else result >= TextToSpeech.LANG_AVAILABLE
                }
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
        applyVoice()
    }

    /**
     * Elige una voz por su nombre técnico; null vuelve a la voz predeterminada del idioma.
     */
    fun setVoice(name: String?) {
        voiceName = name
        if (!isInitialized) return
        if (name == null) {
            tts?.setLanguage(currentLanguage.locale)
        } else {
            applyVoice()
        }
    }

    private fun applyVoice() {
        val name = voiceName ?: return
        val voice = runCatching { tts?.voices }.getOrNull()?.find { it.name == name } ?: return
        tts?.voice = voice
    }

    /**
     * Voces instaladas para [language], con un nombre legible para mostrar.
     * Si no hay voces de la variante regional, se usan las del idioma genérico.
     */
    fun voiceOptions(language: AppLanguage): List<VoiceOption> {
        if (!isInitialized) return emptyList()
        val installed = runCatching { tts?.voices }.getOrNull().orEmpty()
            .filter { TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED !in it.features.orEmpty() }
        val sameLanguage = installed.filter { it.locale.language == language.locale.language }
        val sameCountry = sameLanguage.filter { it.locale.country == language.locale.country }
        return (sameCountry.ifEmpty { sameLanguage })
            .sortedWith(compareBy<Voice>({ it.isNetworkConnectionRequired }, { it.name }))
            .mapIndexed { index, voice -> VoiceOption(voice.name, readableVoiceLabel(index, voice)) }
    }

    /**
     * Indica si faltan datos de voz para [language] en el motor.
     */
    fun isLanguageDataMissing(language: AppLanguage): Boolean {
        if (!isInitialized) return false
        return tts?.isLanguageAvailable(language.locale) == TextToSpeech.LANG_MISSING_DATA
    }

    private fun readableVoiceLabel(index: Int, voice: Voice): String {
        val region = voice.locale.getDisplayCountry(Locale("es"))
        val parts = mutableListOf("Voz ${index + 1}")
        if (region.isNotBlank()) parts += region
        parts += if (voice.isNetworkConnectionRequired) "requiere internet" else "sin internet"
        return parts.joinToString(" · ")
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
