package com.sindicato.aplicacionaccesible.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sindicato.aplicacionaccesible.R
import com.sindicato.aplicacionaccesible.ui.sound.TTSManager
import com.sindicato.aplicacionaccesible.ui.theme.AppTheme
import com.sindicato.aplicacionaccesible.viewmodel.SoundManagerViewModel
import com.sindicato.aplicacionaccesible.viewmodel.SoundboardViewModel

@Composable
fun SettingsDialog(
    soundboardViewModel: SoundboardViewModel,
    soundManagerViewModel: SoundManagerViewModel,
    onDismiss: () -> Unit
) {
    val currentPitch by soundManagerViewModel.ttsPitch.collectAsState()
    val currentSpeechRate by soundManagerViewModel.ttsSpeechRate.collectAsState()
    val savedTheme by soundboardViewModel.appTheme.collectAsState()
    val currentTheme = savedTheme ?: if (isSystemInDarkTheme()) AppTheme.DARK else AppTheme.LIGHT

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Configuración",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // SECCIÓN 1: Temas
                    Column {
                        Text("Tema", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeButton(
                                text = "Claro",
                                icon = Icons.Default.LightMode,
                                isSelected = currentTheme == AppTheme.LIGHT,
                                onClick = { soundboardViewModel.setAppTheme(AppTheme.LIGHT) },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeButton(
                                text = "Oscuro",
                                icon = Icons.Default.DarkMode,
                                isSelected = currentTheme == AppTheme.DARK,
                                onClick = { soundboardViewModel.setAppTheme(AppTheme.DARK) },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeButton(
                                text = "Daltónico",
                                icon = Icons.Default.Contrast,
                                isSelected = currentTheme == AppTheme.COLORBLIND,
                                onClick = { soundboardViewModel.setAppTheme(AppTheme.COLORBLIND) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // SECCIÓN 2: Idioma
                    val selectedLanguage by soundManagerViewModel.selectedLanguage.collectAsState()
                    val availableLanguages by TTSManager.availableLanguages.collectAsState()
                    var expanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        OutlinedCard(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Idioma: ${selectedLanguage.displayName}", // Same text logic
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            // SCALABLE: Just iterate through the Enum
                            availableLanguages.forEach { language ->
                                DropdownMenuItem(
                                    text = { Text(language.displayName) },
                                    onClick = {
                                        soundManagerViewModel.updateLanguage(language)
                                        expanded = false
                                    },
                                    // Visual feedback for selection
                                    trailingIcon = {
                                        if (language == selectedLanguage) {
                                            Icon(Icons.Default.Check, null)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // SECCIÓN 3: Configuración de Voz
                    Column {
                        Text("Configuración de Voz", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(16.dp))
                        
                        // TONO (Pitch)
                        Text("Tono", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Más grave", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Más agudo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        
                        PitchSelector(
                            currentPitch = currentPitch,
                            onPitchSelected = { soundManagerViewModel.setPitch(it) }
                        )

                        Spacer(Modifier.height(24.dp))
                        
                        // VOZ
                        VoiceSelector(soundManagerViewModel)

                        Spacer(Modifier.height(24.dp))

                        // VELOCIDAD (Speech Rate)
                        Text("Velocidad: ${String.format("%.1f", currentSpeechRate)}")
                        Slider(
                            value = currentSpeechRate,
                            onValueChange = { soundManagerViewModel.setSpeechRate(it) },
                            valueRange = 0.5f..2.0f
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceSelector(soundManagerViewModel: SoundManagerViewModel) {
    val context = LocalContext.current
    val isReady by TTSManager.isReady.collectAsState()
    val language by soundManagerViewModel.selectedLanguage.collectAsState()
    val selectedVoice by soundManagerViewModel.selectedVoice.collectAsState()
    val voices = remember(isReady, language) { TTSManager.voiceOptions(language) }
    val dataMissing = remember(isReady, language) { TTSManager.isLanguageDataMissing(language) }

    Text(stringResource(R.string.voice_title), style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))

    when {
        !isReady -> Text(stringResource(R.string.voice_loading), style = MaterialTheme.typography.bodyMedium)

        voices.isEmpty() || dataMissing -> {
            Text(
                stringResource(R.string.voice_missing, language.displayName),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { openTtsSettings(context) }) {
                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.voice_open_settings))
            }
        }

        else -> Column(modifier = Modifier.selectableGroup()) {
            VoiceRow(
                label = stringResource(R.string.voice_default),
                selected = selectedVoice == null,
                onSelect = { soundManagerViewModel.selectVoice(null) },
                onPreview = { soundManagerViewModel.previewVoice(null) }
            )
            voices.forEach { voice ->
                VoiceRow(
                    label = voice.label,
                    selected = selectedVoice == voice.name,
                    onSelect = { soundManagerViewModel.selectVoice(voice.name) },
                    onPreview = { soundManagerViewModel.previewVoice(voice.name) }
                )
            }
        }
    }
}

@Composable
private fun VoiceRow(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = null)
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
        IconButton(onClick = onPreview, modifier = Modifier.size(48.dp)) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = stringResource(R.string.preview_sound, label)
            )
        }
    }
}

/**
 * Abre la configuración de texto a voz del sistema, o la instalación de datos de voz.
 */
private fun openTtsSettings(context: Context) {
    val intents = listOf(
        Intent("com.android.settings.TTS_SETTINGS"),
        Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
    )
    for (intent in intents) {
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (_: ActivityNotFoundException) {
            // Se prueba la siguiente opción
        }
    }
}

val pitchOptions = listOf(
    0.5f to R.string.pitch_very_low,
    0.75f to R.string.pitch_low,
    1.0f to R.string.pitch_normal,
    1.5f to R.string.pitch_high,
    2.0f to R.string.pitch_very_high
)

@Composable
fun PitchSelector(
    currentPitch: Float,
    onPitchSelected: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        pitchOptions.forEach { (pitch, labelRes) ->
            val isSelected = currentPitch == pitch
            val label = stringResource(labelRes)
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .selectable(
                        selected = isSelected,
                        role = Role.RadioButton,
                        onClick = { onPitchSelected(pitch) }
                    )
                    .semantics { contentDescription = label }
                    .testTag("pitch_$pitch"),
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                border = BorderStroke(
                    2.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            ) {}
        }
    }
}

@Composable
fun ThemeButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(text, style = MaterialTheme.typography.labelSmall)
        }
    }
}
