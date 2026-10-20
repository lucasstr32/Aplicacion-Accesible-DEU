package com.sindicato.aplicacionaccesible.ui.signlanguage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SignLanguage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sindicato.aplicacionaccesible.R
import com.sindicato.aplicacionaccesible.data.SignLanguageEntity
import com.sindicato.aplicacionaccesible.ui.theme.SafeColors
import com.sindicato.aplicacionaccesible.ui.theme.getContrastColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignLanguageGrid(
    isColorblindMode: Boolean = false,
    viewModel: SignLanguageViewModel = viewModel()
) {
    val signItems by viewModel.signItems.collectAsState()
    var selectedItem by rememberSaveable { mutableStateOf<SignLanguageEntity?>(null) }
    
    if (selectedItem == null) {
        // Misma forma, tamaño, tipografía y paleta que los botones de Sonidos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(signItems) { index, item ->
                val backgroundColor = SafeColors[index % SafeColors.size]
                val contentColor = getContrastColor(backgroundColor)
                Surface(
                    onClick = { selectedItem = item },
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = backgroundColor,
                    contentColor = contentColor,
                    tonalElevation = 4.dp,
                    border = if (isColorblindMode) BorderStroke(2.dp, contentColor) else null
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SignLanguage,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = item.word,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    } else {
        // Vista de Detalle con la imagen de la seña desde la base de datos
        SignLanguageDetail(item = selectedItem!!, onBack = { selectedItem = null })
    }
}

@Composable
fun SignLanguageDetail(item: SignLanguageEntity, onBack: () -> Unit) {
    val context = LocalContext.current
    val imageRes = remember(item.imageName) {
        val resId = context.resources.getIdentifier(item.imageName, "drawable", context.packageName)
        if (resId != 0) resId else R.drawable.ic_launcher_foreground
    }

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
            Text(text = item.word, style = MaterialTheme.typography.headlineMedium)
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
                    painter = painterResource(id = imageRes),
                    contentDescription = "Seña de ${item.word}",
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Representación en lengua de señas para '${item.word}'.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Espacio para futura funcionalidad de audio (mencionada por el usuario)
        if (item.audioName != null) {
            Button(onClick = { /* Implementar reproducción de audio */ }) {
                Text("Escuchar pronunciación")
            }
        }
    }
}
