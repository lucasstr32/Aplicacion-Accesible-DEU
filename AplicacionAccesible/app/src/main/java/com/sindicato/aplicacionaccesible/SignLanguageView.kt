package com.sindicato.aplicacionaccesible

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignLanguageGrid() {
    var selectedWord by rememberSaveable { mutableStateOf<String?>(null) }
    
    val signs = listOf(
        "Hola", "Yo", "Gracias", "De nada", "Chau", "Saludar",
        "Nombre", "Apellido", "Interprete", "Oyente",
        "Buenos Días", "Buenas Tardes", "Buenas Noches", "Sordo/a"
    )

    if (selectedWord == null) {
        // Vista de Grilla con Texto (Actuando como iconos)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(signs) { sign ->
                Card(
                    onClick = { selectedWord = sign },
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sign,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    } else {
        // Vista de Detalle con la imagen de la seña
        SignLanguageDetail(word = selectedWord!!, onBack = { selectedWord = null })
    }
}

@Composable
fun SignLanguageDetail(word: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = word, style = MaterialTheme.typography.headlineMedium)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = getDrawableId(word)),
                    contentDescription = "Seña de $word",
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Representación en lengua de señas para '$word'.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

fun getDrawableId(word: String): Int {
    return when (word.lowercase().replace(" ", "").replace("/", "").replace("í", "i").replace("á", "a")) {
        "hola" -> R.drawable.hola
        "yo" -> R.drawable.yo
        "gracias" -> R.drawable.gracias
        "denada" -> R.drawable.denada
        "chau" -> R.drawable.chau
        "saludar" -> R.drawable.saludar
        "nombre" -> R.drawable.nombre
        "apellido" -> R.drawable.apellido
        "interprete" -> R.drawable.interprete
        "oyente" -> R.drawable.oyente
        "buenosdias" -> R.drawable.buenosdias
        "buenastardes" -> R.drawable.buenastardes
        "buenasnoches" -> R.drawable.buenasnoches
        "sordo" -> R.drawable.sordo
        "sordoa" -> R.drawable.sordo
        else -> R.drawable.ic_launcher_foreground
    }
}
