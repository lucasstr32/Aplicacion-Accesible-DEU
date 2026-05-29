package com.sindicato.aplicacionaccesible.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.viewmodel.SoundboardViewModel
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.Template


@Composable
@Preview()
fun SoundboardPreview(){
    val viewModel = SoundboardViewModel()
    Soundboard(viewModel)
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Soundboard(viewModel: SoundboardViewModel){
    Scaffold(
        topBar = { SoundboardTopBar(viewModel) },
        content = { padding -> 
            Box(modifier = Modifier.padding(padding)) {
                SoundGrid(viewModel)
            }
        },
    )
}

@Composable
fun SoundGrid(viewModel: SoundboardViewModel) {
    val context = LocalContext.current
    val currentTemplate = viewModel.templates.getOrNull(viewModel.currentTemplateIndex)
    val totalCells = 20

    var showDialogAtPosition by remember { mutableStateOf<Int?>(null) }



    showDialogAtPosition?.let { position ->
        AddButtonDialog(
            onDismiss = { showDialogAtPosition = null },
            onConfirm = { name, effect ->
                viewModel.addButtonToCurrentTemplate(name, effect, position)
                showDialogAtPosition = null
            }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(viewModel.columnCount),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(totalCells) { index ->
            val buttonAtPosition = currentTemplate?.buttons?.find { it.gridPosition == index }

            if (buttonAtPosition != null) {
                SoundButtonItem(
                    name = buttonAtPosition.name,
                    onClick = {
                        if (!viewModel.isEditMode) {
                            viewModel.playSound(context, buttonAtPosition.soundEffect)
                        }
                    }
                )
            } else if (viewModel.isEditMode) {
                EmptyCellPlaceholder(onClick = { showDialogAtPosition = index })
            }
        }
    }
}

@Composable
fun AddButtonDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, SoundEffect) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedEffect by remember { mutableStateOf(SoundEffect.BOMB) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Sound Button") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Button Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedEffect.displayName)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SoundEffect.entries.forEach { effect ->
                            DropdownMenuItem(
                                text = { Text(effect.displayName) },
                                onClick = {
                                    selectedEffect = effect
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedEffect)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
                Text(if (viewModel.isEditMode) "Done" else "Edit")
            }
        }
    )
}


@Composable
fun SoundButtonItem(name: String, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = name)
    }
}


@Composable
fun EmptyCellPlaceholder(onClick: () -> Unit) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add button")
    }
}