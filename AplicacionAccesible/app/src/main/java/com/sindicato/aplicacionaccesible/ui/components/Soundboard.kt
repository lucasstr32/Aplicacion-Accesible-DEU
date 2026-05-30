package com.sindicato.aplicacionaccesible.ui.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.viewmodel.SoundboardViewModel
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.Template
import com.sindicato.aplicacionaccesible.ui.theme.AppTheme
import com.sindicato.aplicacionaccesible.ui.theme.SafeColors
import com.sindicato.aplicacionaccesible.ui.theme.getContrastColor


@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview()
fun SoundboardPreview(){
    val viewModel: SoundboardViewModel = viewModel()
    Soundboard(viewModel, false)
}


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Soundboard(viewModel: SoundboardViewModel, isColorblindMode: Boolean){
    Scaffold(
        topBar = { SoundboardTopBar(viewModel) },
        content = { padding -> 
            Box(modifier = Modifier.padding(padding)) {
                SoundGrid(viewModel, isColorblindMode)
            }
        },
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SoundGrid(viewModel: SoundboardViewModel, isColorblindMode: Boolean) {
    val context = LocalContext.current
    val currentTemplate = viewModel.templates.getOrNull(viewModel.currentTemplateIndex)
    val totalCells = 20

    var showDialogAtPosition by remember { mutableStateOf<Int?>(null) }



    showDialogAtPosition?.let { position ->
        AddButtonDialog(
            onDismiss = { showDialogAtPosition = null },
            onConfirm = { name, effect, selectedColor, selectedIcon ->
                viewModel.addButtonToCurrentTemplate(name, effect, selectedColor, selectedIcon, position)
                showDialogAtPosition = null
            },
            soundboardViewModel = viewModel
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
                    button = buttonAtPosition,
                    isColorblindMode = isColorblindMode,
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
fun SoundButtonItem(
    button: SoundButton,
    isColorblindMode: Boolean,
    onClick: () -> Unit) {


    val backgroundColor = Color(button.color)
    val contentColor = getContrastColor(backgroundColor)


    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = if (isColorblindMode) BorderStroke(2.dp, contentColor) else null
        ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = availableIcons[button.iconRes],
                contentDescription = null, // Faltaría añadir content description
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = button.name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
        }
    }
}



@Preview(name = "Add Button Dialog", showBackground = true)
@Composable
fun AddButtonDialogPreview() {
    AddButtonDialog(
        onDismiss = { /* Do nothing */ },
        onConfirm = { name, effect, color, iconRes -> println("Preview: $name with $effect")},
        soundboardViewModel = viewModel()
    )
}



val buttonColors = listOf(
    Color(0xFFEF5350), Color(0xFF66BB6A), Color(0xFF42A5F5),
    Color(0xFFFFCA28), Color(0xFFAB47BC), Color(0xFF26A69A)
)

// Define a list of selectable icons
val availableIcons = listOf(
    Icons.Default.MusicNote, Icons.Default.Notifications,
    Icons.Default.Favorite, Icons.Default.Star,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddButtonDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, SoundEffect, Long, Int) -> Unit,
    soundboardViewModel: SoundboardViewModel
) {
    var name by remember { mutableStateOf("") }
    var selectedEffect by remember { mutableStateOf(SoundEffect.BOMB) }
    var expanded by remember { mutableStateOf(false) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Button", "Speech")

    var iconExpanded by remember { mutableStateOf(false) } // State for icon dropdown
    var selectedIconIndex by remember { mutableIntStateOf(0) }

    var selectedColor by remember { mutableStateOf(buttonColors[0]) }

    val context = LocalContext.current


    AlertDialog(
        onDismissRequest = onDismiss,
        title = {

            Column {
                Text("Add Element")
                // TabRow for switching between Button and Speech
                androidx.compose.material3.TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        androidx.compose.material3.Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                if(selectedTabIndex == 0) {
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
                                    text = {
                                        // Layout the name and the play button side-by-side
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(effect.displayName, modifier = Modifier.weight(1f))

                                            // The Preview Play Button
                                            IconButton(
                                                onClick = {
                                                    // This plays the sound without closing the menu
                                                    soundboardViewModel.playSound(context, effect)
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "Preview sound",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedEffect = effect
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text("Select Icon", style = MaterialTheme.typography.labelMedium)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { iconExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = availableIcons[selectedIconIndex],
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Choose Icon")
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = iconExpanded,
                            onDismissRequest = { iconExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // Adjust width as needed
                                .padding(8.dp)
                        ) {
                            // Using FlowRow inside the Dropdown to create the Grid effect
                            // This is scalable: as you add more icons, they wrap automatically
                            FlowRow(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                maxItemsInEachRow = 4 // Control how many columns
                            ) {
                                availableIcons.forEachIndexed { index, icon ->
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                color = if (selectedIconIndex == index)
                                                    MaterialTheme.colorScheme.primaryContainer
                                                else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                selectedIconIndex = index
                                                iconExpanded = false
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (selectedIconIndex == index)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text("Select Color", style = MaterialTheme.typography.labelMedium)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(SafeColors.size) { index ->
                            val color = SafeColors[index]
                            Surface(
                                modifier = Modifier
                                    .size(35.dp)
                                    .clickable { selectedColor = color },
                                shape = CircleShape,
                                color = color,
                                border = if (selectedColor == color)
                                    BorderStroke(3.dp, Color.Black) else null
                            ) {}
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text("Speech configuration coming soon...", color = Color.Gray)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val colorLong = selectedColor.toArgb().toLong()

                        onConfirm(name, selectedEffect,
                            colorLong, selectedIconIndex)
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