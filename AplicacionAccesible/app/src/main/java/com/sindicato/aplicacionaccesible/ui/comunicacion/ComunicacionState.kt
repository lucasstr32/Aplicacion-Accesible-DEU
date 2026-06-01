package com.sindicato.aplicacionaccesible.ui.comunicacion

import com.sindicato.aplicacionaccesible.data.PhraseEntity

enum class ComunicacionMode {
    TEXT_TO_SPEECH, SPEECH_TO_TEXT
}

enum class TtsStatus {
    IDLE, REPRODUCIENDO, CARGANDO, ERROR
}

enum class SttStatus {
    IDLE, ESCUCHANDO, PROCESANDO, ERROR
}

data class ComunicacionUiState(
    val mode: ComunicacionMode = ComunicacionMode.TEXT_TO_SPEECH,
    val ttsText: String = "",
    val ttsStatus: TtsStatus = TtsStatus.IDLE,
    val sttText: String = "",
    val sttStatus: SttStatus = SttStatus.IDLE,
    val errorMessage: String? = null,
    val savedPhrases: List<PhraseEntity> = emptyList()
)
