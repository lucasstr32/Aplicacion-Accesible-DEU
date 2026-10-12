package com.sindicato.aplicacionaccesible

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignLanguage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sindicato.aplicacionaccesible.data.AppDatabase
import com.sindicato.aplicacionaccesible.data.DatabaseSeeder
import com.sindicato.aplicacionaccesible.data.repository.ButtonRepository
import com.sindicato.aplicacionaccesible.data.repository.TemplateRepository
import com.sindicato.aplicacionaccesible.data.settings.SettingsRepository
import com.sindicato.aplicacionaccesible.ui.components.SettingsDialog
import com.sindicato.aplicacionaccesible.ui.components.Soundboard
import com.sindicato.aplicacionaccesible.ui.comunicacion.ComunicacionScreen
import com.sindicato.aplicacionaccesible.ui.comunicacion.ComunicacionViewModel
import com.sindicato.aplicacionaccesible.viewmodel.SoundboardViewModel
import com.sindicato.aplicacionaccesible.ui.signlanguage.SignLanguageGrid
import com.sindicato.aplicacionaccesible.ui.sound.SoundEffectManager
import com.sindicato.aplicacionaccesible.ui.sound.TTSManager
import com.sindicato.aplicacionaccesible.ui.theme.AppTheme
import com.sindicato.aplicacionaccesible.ui.theme.AplicacionAccesibleTheme
import com.sindicato.aplicacionaccesible.viewmodel.SoundManagerViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {



    lateinit var templateRepository: TemplateRepository
        private set

    lateinit var buttonRepository: ButtonRepository
        private set




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        SoundEffectManager.init(this)
        TTSManager.init(this)

        // Seed the database
        val signLanguageDao = AppDatabase.getDatabase(this).signLanguageDao()
        val templateDao = AppDatabase.getDatabase(this).templateDao()
        val buttonDao = AppDatabase.getDatabase(this).buttonDao()
        lifecycleScope.launch {
            DatabaseSeeder.seedDatabase(signLanguageDao, templateDao, buttonDao)
        }

        val database = AppDatabase.getDatabase(this)

        templateRepository = TemplateRepository(database.templateDao())
        buttonRepository = ButtonRepository(database.buttonDao())
        val settingsRepository = SettingsRepository(applicationContext)



        setContent {

            val soundboardViewModel: SoundboardViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SoundboardViewModel(templateRepository, buttonRepository, settingsRepository) as T
                    }
                }
            )
            // viewModel() conserva las instancias entre recomposiciones (p. ej. al cambiar
            // el tema) y rotaciones; antes se creaban de nuevo y se perdían los ajustes.
            val soundManagerViewModel: SoundManagerViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SoundManagerViewModel(settingsRepository) as T
                    }
                }
            )
            val comunicacionViewModel: ComunicacionViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ComunicacionViewModel(applicationContext) as T
                    }
                }
            )

            val savedTheme by soundboardViewModel.appTheme.collectAsState()
            // Mientras no se elija un tema, se sigue el claro/oscuro del sistema
            val currentTheme = savedTheme ?: if (isSystemInDarkTheme()) AppTheme.DARK else AppTheme.LIGHT

            AplicacionAccesibleTheme(appTheme = currentTheme) {
                MainScreen(
                    soundboardViewModel = soundboardViewModel,
                    soundManagerViewModel = soundManagerViewModel,
                    comunicacionViewModel = comunicacionViewModel
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundEffectManager.release()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    soundboardViewModel: SoundboardViewModel,
    soundManagerViewModel: SoundManagerViewModel,
    comunicacionViewModel: ComunicacionViewModel
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val helpContent = when (selectedTab) {
        0 -> "Sonidos" to "Esta pantalla te permite reproducir sonidos comunes. Toca cualquier botón para escuchar el sonido correspondiente. Puedes cambiar el número de columnas con el botón de columnas del menú superior."
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
                    Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Ayuda: ${helpContent.first}")
                }
            },
            text = { Text(helpContent.second) }
        )
    }

    if (showSettings) {
        SettingsDialog(
            soundboardViewModel = soundboardViewModel,
            soundManagerViewModel = soundManagerViewModel,
            onDismiss = { showSettings = false }
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
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Help,
                            contentDescription = stringResource(R.string.help)
                        )
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración")
                    }

                    // Botón para cambiar el número de columnas en la grilla
                    if (selectedTab == 0) {
                        IconButton(onClick = {
                            val columnCount = soundboardViewModel.columnCount
                            val nextCols = if (columnCount >= 4) 2 else columnCount + 1
                            soundboardViewModel.updateColumnCount(nextCols)
                        }) {
                            Icon(Icons.Default.CalendarViewMonth, contentDescription = "Columnas: ${soundboardViewModel.columnCount}")
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
                    icon = { Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null) },
                    label = { Text("Sonidos") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                    label = { Text("Comunicación") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.SignLanguage, contentDescription = null) },
                    label = { Text("Señas") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            when (selectedTab) {
                0 -> Soundboard(
                    soundboardViewModel,
                    soundManagerViewModel,
                    soundboardViewModel.appTheme.collectAsState().value == AppTheme.COLORBLIND,
                )
                1 -> ComunicacionScreen(
                    comunicacionViewModel,
                    soundManagerViewModel
                )
                2 -> SignLanguageGrid(
                    isColorblindMode = soundboardViewModel.appTheme.collectAsState().value == AppTheme.COLORBLIND
                )
            }
        }
    }
}

@Preview(
    name = "Small phone",
    device = Devices.PIXEL_4A,
    showBackground = true
)
@Preview(
    name = "Tablet",
    device = Devices.NEXUS_10,
    showBackground = true,
    widthDp = 800,
    heightDp = 1280
)
@Composable
fun MainScreenPreview() {
    val soundboardViewModel: SoundboardViewModel = viewModel()
    val soundManagerViewModel: SoundManagerViewModel = viewModel()
    val comunicacionViewModel: ComunicacionViewModel = viewModel()
    MainScreen(
        soundboardViewModel = soundboardViewModel,
        soundManagerViewModel = soundManagerViewModel,
        comunicacionViewModel = comunicacionViewModel
    )
}
