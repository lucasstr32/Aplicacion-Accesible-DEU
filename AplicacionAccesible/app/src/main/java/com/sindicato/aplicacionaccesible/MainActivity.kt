package com.sindicato.aplicacionaccesible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.sindicato.aplicacionaccesible.data.AppDatabase
import com.sindicato.aplicacionaccesible.data.DatabaseSeeder
import com.sindicato.aplicacionaccesible.ui.comunicacion.ComunicacionScreen
import com.sindicato.aplicacionaccesible.ui.signlanguage.SignLanguageGrid
import com.sindicato.aplicacionaccesible.ui.theme.AppTheme
import com.sindicato.aplicacionaccesible.ui.theme.AplicacionAccesibleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Seed the database
        val dao = AppDatabase.getDatabase(applicationContext).signLanguageDao()
        lifecycleScope.launch {
            DatabaseSeeder.seedDatabase(dao)
        }
        
        setContent {
            var currentTheme by rememberSaveable { mutableStateOf(AppTheme.LIGHT) }
            var columnCount by rememberSaveable { mutableIntStateOf(2) }

            AplicacionAccesibleTheme(appTheme = currentTheme) {
                MainScreen(
                    currentTheme = currentTheme,
                    onThemeChange = { currentTheme = it },
                    columnCount = columnCount,
                    onColumnCountChange = { columnCount = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    columnCount: Int,
    onColumnCountChange: (Int) -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val helpContent = when (selectedTab) {
        0 -> "Sonidos" to "Esta pantalla te permite reproducir sonidos comunes. Toca cualquier botón para escuchar el sonido correspondiente. Puedes cambiar el número de columnas desde el menú superior (icono de tres líneas)."
        1 -> "Comunicación" to "Aquí tienes dos modos: 'Texto a Voz' para escribir lo que quieres que la app diga, y 'Voz a Texto' para que la app transcriba lo que hablas. Cambia entre ellos usando el selector superior."
        else -> "Lenguaje de Señas" to "Explora palabras en lenguaje de señas. Toca una palabra en la lista para ver su representación visual. Puedes volver a la lista usando el botón de retroceso en el detalle."
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Entendido")
                }
            },
            title = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Help, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Ayuda: ${helpContent.first}")
                }
            },
            text = { Text(helpContent.second) }
        )
    }

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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showHelpDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Help,
                    contentDescription = "Botón de Ayuda"
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> SoundGrid(columnCount)
                1 -> ComunicacionScreen()
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
