package com.sindicato.aplicacionaccesible.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sindicato.aplicacionaccesible.ui.sound.AppLanguage
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
    val currentTheme by soundboardViewModel.appTheme.collectAsState()

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
                            AppLanguage.entries.forEach { language ->
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
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val pitchOptions = listOf(0.5f, 0.75f, 1.0f, 1.5f, 2.0f)
                            pitchOptions.forEach { pitch ->
                                val isSelected = currentPitch == pitch
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { soundManagerViewModel.setPitch(pitch) },
                                    label = { Text(text = "") },
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }
                        
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
