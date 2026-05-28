package com.sindicato.aplicacionaccesible.ui.comunicacion

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComunicacionScreen(
    viewModel: ComunicacionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ModeSelector(
            selectedMode = uiState.mode,
            onModeSelected = { viewModel.changeMode(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = uiState.mode,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "modeTransition"
        ) { mode ->
            when (mode) {
                ComunicacionMode.TEXT_TO_SPEECH -> {
                    TtsSection(
                        text = uiState.ttsText,
                        onTextChanged = { viewModel.onTtsTextChanged(it) },
                        status = uiState.ttsStatus,
                        onSpeak = { viewModel.speak() }
                    )
                }
                ComunicacionMode.SPEECH_TO_TEXT -> {
                    SttSection(
                        text = uiState.sttText,
                        status = uiState.sttStatus,
                        onMicClick = {
                            val permissionCheckResult = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            )
                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                if (uiState.sttStatus == SttStatus.ESCUCHANDO) {
                                    viewModel.stopListening()
                                } else {
                                    viewModel.startListening()
                                }
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    )
                }
            }
        }

        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ModeSelector(
    selectedMode: ComunicacionMode,
    onModeSelected: (ComunicacionMode) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        SegmentedButton(
            selected = selectedMode == ComunicacionMode.TEXT_TO_SPEECH,
            onClick = { onModeSelected(ComunicacionMode.TEXT_TO_SPEECH) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            icon = { Icon(Icons.Default.Edit, contentDescription = null) }
        ) {
            Text("Texto a Voz")
        }
        SegmentedButton(
            selected = selectedMode == ComunicacionMode.SPEECH_TO_TEXT,
            onClick = { onModeSelected(ComunicacionMode.SPEECH_TO_TEXT) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            icon = { Icon(Icons.Default.Mic, contentDescription = null) }
        ) {
            Text("Voz a Texto")
        }
    }
}

@Composable
fun TtsSection(
    text: String,
    onTextChanged: (String) -> Unit,
    status: TtsStatus,
    onSpeak: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            placeholder = { Text("Escribe el texto que quieres que la app diga...") },
            label = { Text("Mensaje") },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSpeak,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = text.isNotBlank() && status != TtsStatus.CARGANDO,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (status == TtsStatus.REPRODUCIENDO) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reproduciendo...")
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Escuchar texto")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        InfoBox(
            title = "Consejo",
            description = "Puedes escribir frases largas y la aplicación las leerá con una voz clara."
        )
    }
}

@Composable
fun SttSection(
    text: String,
    status: SttStatus,
    onMicClick: () -> Unit
) {
    var showFullTextDialog by remember { mutableStateOf(false) }

    if (showFullTextDialog && text.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showFullTextDialog = false },
            confirmButton = {
                TextButton(onClick = { showFullTextDialog = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Texto reconocido") },
            text = {
                Text(
                    text = text,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            onClick = { if (text.isNotEmpty()) showFullTextDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (text.isEmpty() && status == SttStatus.IDLE) {
                    Text(
                        "Presiona el micrófono y empieza a hablar",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        maxLines = 5,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        FloatingActionButton(
            onClick = onMicClick,
            containerColor = if (status == SttStatus.ESCUCHANDO) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,
            contentColor = if (status == SttStatus.ESCUCHANDO) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(40.dp)
        ) {
            Icon(
                imageVector = if (status == SttStatus.ESCUCHANDO) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = "Micrófono",
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (status) {
                SttStatus.ESCUCHANDO -> "Escuchando..."
                SttStatus.PROCESANDO -> "Procesando..."
                else -> "Toca para hablar"
            },
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InfoBox(title: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
