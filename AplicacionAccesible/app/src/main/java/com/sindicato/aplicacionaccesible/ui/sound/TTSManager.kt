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
    var language = Locale("es", "ES")
    private var speechRate = 1.0F
    private var pitch = 1.0F


    val ttsText: String = ""
    var ttsStatus: TtsStatus = TtsStatus.IDLE
    var sttText: String = ""
    var sttStatus: SttStatus = SttStatus.IDLE
    var errorMessage: String? = null
    var savedPhrases: List<PhraseEntity> = emptyList()


    fun init(context: Context) {
        Log.d("TTSManager", "Inicializando TTS. Estado: $isInitialized")
        if(isInitialized) return

        setupTts(context)

        isInitialized = true
        tts?.setPitch(1.0F)
        tts?.setSpeechRate(1.0F)
        Log.d("TTSManager", "TTS Inicializado. Estado: ${tts?.voice}")

    }



    private fun setupTts(context: Context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
//                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
//
//
//                    @Deprecated("Deprecated in Java")
//                    override fun onError(utteranceId: String?) {
//                        ttsStatus = TtsStatus.ERROR
//                    }
//
//                    override fun onError(utteranceId: String?, errorCode: Int) {
//                        ttsStatus = TtsStatus.ERROR
//                    }
//                })
//                    ttsStatus = TtsStatus.IDLE
//            } else {
//                ttsStatus = TtsStatus.ERROR
            }
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

    fun getPitch(): Float{
        return pitch
    }

    fun getSpeechRate(): Float{
        return speechRate
    }

    fun setSpeechRate(rate: Float){
        speechRate = rate
        tts?.setSpeechRate(rate)
    }

    fun release(){
        tts?.stop()
        tts?.shutdown()
        isInitialized = false
    }



}