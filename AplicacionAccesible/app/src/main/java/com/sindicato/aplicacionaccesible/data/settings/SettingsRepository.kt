package com.sindicato.aplicacionaccesible.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sindicato.aplicacionaccesible.ui.sound.AppLanguage
import com.sindicato.aplicacionaccesible.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Ajustes de la app que sobreviven al cierre.
 * [theme] es null mientras la persona no haya elegido uno: se sigue el tema del sistema.
 */
data class AppSettings(
    val theme: AppTheme? = null,
    val language: AppLanguage = AppLanguage.SPANISH,
    val pitch: Float = 1.0f,
    val speechRate: Float = 1.0f,
    val columnCount: Int = 2,
    val voiceName: String? = null
)

object SettingsKeys {
    val THEME = stringPreferencesKey("theme")
    val LANGUAGE = stringPreferencesKey("language")
    val PITCH = floatPreferencesKey("pitch")
    val SPEECH_RATE = floatPreferencesKey("speech_rate")
    val COLUMN_COUNT = intPreferencesKey("column_count")
    val VOICE = stringPreferencesKey("voice")
}

/**
 * Convierte las preferencias guardadas en [AppSettings], ignorando valores inválidos.
 */
fun Preferences.toAppSettings(): AppSettings {
    val defaults = AppSettings()
    return AppSettings(
        theme = this[SettingsKeys.THEME]?.let { name -> AppTheme.entries.find { it.name == name } },
        language = this[SettingsKeys.LANGUAGE]
            ?.let { name -> AppLanguage.entries.find { it.name == name } }
            ?: defaults.language,
        pitch = this[SettingsKeys.PITCH] ?: defaults.pitch,
        speechRate = this[SettingsKeys.SPEECH_RATE] ?: defaults.speechRate,
        columnCount = this[SettingsKeys.COLUMN_COUNT]?.takeIf { it in 2..4 } ?: defaults.columnCount,
        voiceName = this[SettingsKeys.VOICE]
    )
}

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    constructor(context: Context) : this(context.applicationContext.settingsDataStore)

    val settings: Flow<AppSettings> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { it.toAppSettings() }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { it[SettingsKeys.THEME] = theme.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[SettingsKeys.LANGUAGE] = language.name }
    }

    suspend fun setPitch(pitch: Float) {
        dataStore.edit { it[SettingsKeys.PITCH] = pitch }
    }

    suspend fun setSpeechRate(rate: Float) {
        dataStore.edit { it[SettingsKeys.SPEECH_RATE] = rate }
    }

    suspend fun setVoice(name: String?) {
        dataStore.edit {
            if (name == null) it.remove(SettingsKeys.VOICE) else it[SettingsKeys.VOICE] = name
        }
    }

    suspend fun setColumnCount(count: Int) {
        dataStore.edit { it[SettingsKeys.COLUMN_COUNT] = count }
    }
}
