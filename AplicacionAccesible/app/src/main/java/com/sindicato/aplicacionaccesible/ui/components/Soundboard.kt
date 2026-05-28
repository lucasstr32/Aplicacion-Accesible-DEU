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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    // Add them to the ViewModel
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
            } else if (viewModel.isEditMode) {
                // Empty Cell in Edit Mode
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundboardTopBar(viewModel: SoundboardViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var newTemplateName by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { 
                showDialog = false
                newTemplateName = ""
            },
            title = { Text("New Template") },
            text = {
                TextField(
                    value = newTemplateName,
                    onValueChange = { newTemplateName = it },
                    label = { Text("Template Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTemplateName.isNotBlank()) {
                            viewModel.addTemplate(newTemplateName)
                            showDialog = false
                            newTemplateName = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDialog = false
                    newTemplateName = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Template")
            }
        },
        title = {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
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