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
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sindicato.aplicacionaccesible.data.AppDatabase
import com.sindicato.aplicacionaccesible.data.DatabaseSeeder
import com.sindicato.aplicacionaccesible.ui.components.Soundboard
import com.sindicato.aplicacionaccesible.ui.comunicacion.ComunicacionScreen
import com.sindicato.aplicacionaccesible.viewmodel.SoundboardViewModel
import com.sindicato.aplicacionaccesible.ui.signlanguage.SignLanguageGrid
import com.sindicato.aplicacionaccesible.ui.sound.SoundEffectManager
import com.sindicato.aplicacionaccesible.ui.theme.AppTheme
import com.sindicato.aplicacionaccesible.ui.theme.AplicacionAccesibleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        SoundEffectManager.init(applicationContext)

        // Seed the database
        val dao = AppDatabase.getDatabase(applicationContext).signLanguageDao()
        lifecycleScope.launch {
            DatabaseSeeder.seedDatabase(dao)
        }

        setContent {
            var currentTheme by rememberSaveable { mutableStateOf(AppTheme.LIGHT) }
            var columnCount by rememberSaveable { mutableIntStateOf(2) }
            val soundboardViewModel: SoundboardViewModel = viewModel()

            AplicacionAccesibleTheme(appTheme = currentTheme) {
                MainScreen(
                    currentTheme = currentTheme,
                    onThemeChange = { currentTheme = it },
                    columnCount = columnCount,
                    onColumnCountChange = { columnCount = it },
                    soundboardViewModel = soundboardViewModel
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundEffectManager.release()

    }
}


@Preview()
@Composable
fun MainScreenPreview() {
    val viewModel: SoundboardViewModel = viewModel()
    MainScreen(
        currentTheme = AppTheme.LIGHT,
        onThemeChange = {},
        columnCount = 2,
        onColumnCountChange = {},
        soundboardViewModel = viewModel
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    columnCount: Int,
    onColumnCountChange: (Int) -> Unit,
    soundboardViewModel: SoundboardViewModel
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when(selectedTab) {
                            0 -> "Sonidos"
                            1 -> "Comunicación"
                            else -> "Señas"
                        }
                    ) 
                },
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

                    // Botón para cambiar el número de columnas en la grilla
                    if (selectedTab == 0) {
                        IconButton(onClick = {
                            val nextCols = if (columnCount >= 4) 2 else columnCount + 1 // anillo (2-3-4) que arranca de 2
                            onColumnCountChange(nextCols) // Se actualiza la cantidad de columnas y triggerea recomposición de la UI
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
                    icon = { Icon(Icons.Default.Email, contentDescription = null) },
                    label = { Text("Comunicación") }
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
                0 -> Soundboard(soundboardViewModel)
                1 -> ComunicacionScreen()
                2 -> SignLanguageGrid()
            }
        }
    }
}




