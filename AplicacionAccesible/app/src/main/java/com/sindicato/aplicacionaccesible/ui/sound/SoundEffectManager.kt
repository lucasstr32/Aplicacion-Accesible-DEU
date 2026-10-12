package com.sindicato.aplicacionaccesible.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect

/**
 * Efectos de sonido con SoundPool: se decodifican una vez al iniciar y luego
 * suenan sin la demora de crear un MediaPlayer en cada toque.
 */
object SoundEffectManager {
    private var soundPool: SoundPool? = null
    // Map to store Resource ID -> Loaded Sound ID
    private val soundMap = mutableMapOf<Int, Int>()
    // Sound IDs que SoundPool ya terminó de cargar
    private val loadedSounds = mutableSetOf<Int>()
    private var currentStreamId = 0
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        // USAGE_MEDIA: los efectos son la voz de la persona, así que usan el volumen
        // multimedia (como el TTS) y no se silencian en modo vibración/silencio.
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5) // Allows 5 sounds to play simultaneously
            .setAudioAttributes(audioAttributes)
            .build()
            .apply {
                setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) loadedSounds += sampleId
                    else Log.e("SoundEffectManager", "Error loading sound $sampleId: $status")
                }
            }

        isInitialized = true
        SoundEffect.entries.forEach { loadSound(context.applicationContext, it.resourceId) }
    }

    /**
     * Preloads a sound from res/raw.
     */
    private fun loadSound(context: Context, resId: Int) {
        if (soundMap.containsKey(resId)) return

        val loadedId = soundPool?.load(context, resId, 1) ?: 0
        if (loadedId != 0) {
            soundMap[resId] = loadedId
        }
    }

    /**
     * Plays the sound associated with the resId, stopping the previous one.
     * @return false si el sonido todavía no está cargado.
     */
    fun playSound(resId: Int): Boolean {
        val soundId = soundMap[resId]
        if (soundId == null || soundId !in loadedSounds) {
            Log.w("SoundEffectManager", "Sound not loaded yet for resource ID: $resId")
            return false
        }
        soundPool?.stop(currentStreamId)
        currentStreamId = soundPool?.play(soundId, 1f, 1f, 0, 0, 1f) ?: 0
        return currentStreamId != 0
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
        loadedSounds.clear()
        currentStreamId = 0
        isInitialized = false
    }
}
