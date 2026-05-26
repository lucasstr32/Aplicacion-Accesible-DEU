package com.sindicato.aplicacionaccesible

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sindicato.aplicacionaccesible.ui.theme.AppTheme
import com.sindicato.aplicacionaccesible.ui.theme.AplicacionAccesibleTheme
import java.util.*

class MainActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }

        setContent {
            var currentTheme by rememberSaveable { mutableStateOf(AppTheme.LIGHT) }
            var columnCount by rememberSaveable { mutableIntStateOf(2) }

            AplicacionAccesibleTheme(appTheme = currentTheme) {
                MainScreen(
                    currentTheme = currentTheme,
                    onThemeChange = { currentTheme = it },
                    columnCount = columnCount,
                    onColumnCountChange = { columnCount = it },
                    onSpeak = { text -> tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null) }
                )
            }
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    columnCount: Int,
    onColumnCountChange: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accesible App") },
                actions = {
                    IconButton(onClick = {
                        val nextTheme = when (currentTheme) {
                            AppTheme.LIGHT -> AppTheme.DARK
                            AppTheme.DARK -> AppTheme.COLORBLIND
                            AppTheme.COLORBLIND -> AppTheme.LIGHT
                        }
                        onThemeChange(nextTheme)
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Cambiar Tema")
                    }
                    if (selectedTab == 0) {
                        IconButton(onClick = {
                            val nextCols = if (columnCount >= 4) 2 else columnCount + 1
                            onColumnCountChange(nextCols)
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Columnas: $columnCount")
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    label = { Text("Sonidos") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Face, contentDescription = null) },
                    label = { Text("Texto") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    label = { Text("Señas") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> SoundGrid(columnCount)
                1 -> TextToSpeechSection(onSpeak)
                2 -> SignLanguageGrid()
            }
        }
    }
}

@Composable
fun SoundGrid(columns: Int) {
    val sounds = listOf("Timbre", "Aplauso", "Alarma", "Pito", "Campana", "Grito")
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sounds) { sound ->
            Button(
                onClick = { /* Reproducir sonido */ },
                modifier = Modifier.height(100.dp).fillMaxWidth()
            ) {
                Text(sound)
            }
        }
    }
}

@Composable
fun TextToSpeechSection(onSpeak: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Escribe algo para leer") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSpeak(text) },
            enabled = text.isNotBlank()
        ) {
            Text("Leer en voz alta")
        }
    }
}

@Composable
fun SignLanguageGrid() {
    // Para prototipo, usamos iconos o placeholders si no hay recursos de imagen
    val signs = listOf("Hola", "Gracias", "Por favor", "Adiós", "Ayuda", "Sí", "No")
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(signs) { sign ->
            Card(
                modifier = Modifier.height(150.dp).fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(64.dp))
                    Text(sign, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
