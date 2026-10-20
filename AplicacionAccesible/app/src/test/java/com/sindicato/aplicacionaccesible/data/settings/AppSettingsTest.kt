package com.sindicato.aplicacionaccesible.data.settings

import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import com.sindicato.aplicacionaccesible.ui.sound.AppLanguage
import com.sindicato.aplicacionaccesible.ui.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppSettingsTest {

    @Test
    fun emptyPreferencesUseDefaultsAndFollowSystemTheme() {
        val settings = emptyPreferences().toAppSettings()
        assertNull(settings.theme)
        assertEquals(AppSettings(), settings)
    }

    @Test
    fun savedValuesAreRead() {
        val settings = preferencesOf(
            SettingsKeys.THEME to AppTheme.COLORBLIND.name,
            SettingsKeys.LANGUAGE to AppLanguage.ENGLISH.name,
            SettingsKeys.PITCH to 1.5f,
            SettingsKeys.SPEECH_RATE to 0.8f,
            SettingsKeys.COLUMN_COUNT to 3,
            SettingsKeys.VOICE to "es-es-x-eea-local"
        ).toAppSettings()

        assertEquals(
            AppSettings(AppTheme.COLORBLIND, AppLanguage.ENGLISH, 1.5f, 0.8f, 3, "es-es-x-eea-local"),
            settings
        )
    }

    @Test
    fun invalidValuesFallBackToDefaults() {
        val settings = preferencesOf(
            SettingsKeys.THEME to "NO_EXISTE",
            SettingsKeys.LANGUAGE to "KLINGON",
            SettingsKeys.COLUMN_COUNT to 9
        ).toAppSettings()

        assertNull(settings.theme)
        assertEquals(AppLanguage.SPANISH, settings.language)
        assertEquals(2, settings.columnCount)
    }
}
