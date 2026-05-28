package com.sindicato.aplicacionaccesible.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundboardViewModel
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.Template


@Composable
@Preview()
fun SoundboardPreview(){
    val viewModel = SoundboardViewModel()
    val mockTemplates = listOf(
        Template("Fireside", listOf(SoundButton("Crackle", 0))),
        Template("Cyberpunk", listOf(SoundButton("Neon", 1))),
        Template("Nature", listOf(SoundButton("Rain", 2))),
        Template("Office", listOf(SoundButton("Typewriter", 3))),
        Template("Gym", listOf(SoundButton("Whistle", 4)))
    )

    // 3. Add them to the ViewModel (Assuming you have an add function)
    mockTemplates.forEach { viewModel.addTemplate(it.name) }

    // Set an initial selection
    viewModel.currentTemplateIndex = 0
    Soundboard(viewModel)
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Soundboard(viewModel: SoundboardViewModel){
    Scaffold(
        topBar = { SoundboardTopBar(viewModel) },
        content = { SoundGrid(viewModel) },
    )

}

@Composable
fun SoundGrid(viewModel: SoundboardViewModel) {
    val currentTemplate = viewModel.templates.getOrNull(viewModel.currentTemplateIndex)
    val totalCells = 20 // Define a max capacity

    LazyVerticalGrid(
        columns = GridCells.Fixed(viewModel.columnCount),
        modifier = Modifier.fillMaxSize()
    ) {
        items(totalCells) { index ->
            val buttonAtPosition = currentTemplate?.buttons?.find { it.gridPosition == index }

            if (buttonAtPosition != null) {
                // Occupied Cell
//                DraggableSoundButton(
//                    button = buttonAtPosition,
//                    isEditMode = viewModel.isEditMode,
//                    onDelete = { /* Show Confirmation */ }
//                )
            } else if (viewModel.isEditMode) {
                // Empty Cell in Edit Mode
                //PlaceholderButton(onClick = { /* Show Customization Dialog for index */ })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundboardTopBar(viewModel: SoundboardViewModel) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { /* Show Add Dialog */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Template")
            }
        },
        title = {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between chips
                contentPadding = PaddingValues(horizontal = 8.dp)   // Padding at start/end
            ) {
                itemsIndexed(viewModel.templates) { index, template ->
                    FilterChip(
                        selected = viewModel.currentTemplateIndex == index,
                        onClick = { viewModel.currentTemplateIndex = index },
                        label = { Text(template.name) }
                    )
                }
            }
        },
        actions = {
            TextButton(onClick = { viewModel.toggleEditMode() }) {
                Text(if (viewModel.getIsEditMode()) "Done" else "Edit")
            }
        }
    )
}