package com.sindicato.aplicacionaccesible.ui.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wc
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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sindicato.aplicacionaccesible.R
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffectButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.TTSButton
import com.sindicato.aplicacionaccesible.viewmodel.SoundboardViewModel
import com.sindicato.aplicacionaccesible.ui.theme.SafeColors
import com.sindicato.aplicacionaccesible.ui.theme.getContrastColor
import com.sindicato.aplicacionaccesible.ui.theme.toSafeColor
import com.sindicato.aplicacionaccesible.viewmodel.SoundManagerViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview()
fun SoundboardPreview(){
    val soundboardViewModel: SoundboardViewModel = viewModel()
    val soundManagerViewModel: SoundManagerViewModel = viewModel()
    Soundboard(soundboardViewModel, soundManagerViewModel, false)
}


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Soundboard(
    soundboardViewModel: SoundboardViewModel,
    soundManagerViewModel: SoundManagerViewModel,
    isColorblindMode: Boolean
){
    Scaffold(
        topBar = { SoundboardTopBar(soundboardViewModel) },
        content = { padding -> 
            Box(modifier = Modifier.padding(padding)) {
                SoundGrid(
                    soundboardViewModel,
                    soundManagerViewModel,
                    isColorblindMode)
            }
        },
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SoundGrid(
    soundboardViewModel: SoundboardViewModel,
    soundManagerViewModel: SoundManagerViewModel,
    isColorblindMode: Boolean
) {
    val context = LocalContext.current
    val currentTemplate = soundboardViewModel.templates.getOrNull(soundboardViewModel.currentTemplateIndex)
    val totalCells = 20

    var showDialogAtPosition by remember { mutableStateOf<Int?>(null) }
    var editingButton by remember { mutableStateOf<SoundButton?>(null) }

    /* Add button */
    showDialogAtPosition?.let { position ->
        AddButtonDialog(
            onDismiss = { showDialogAtPosition = null },
            onConfirm = { name, effect, tts, selectedColor, selectedIcon, tabIndex ->
                // Use tabIndex to decide which specific VM function to call
                if (tabIndex == 0 && effect != null) {
                    soundboardViewModel.addSoundEffectButtonToCurrentTemplate(
                        name, effect, selectedColor, selectedIcon, position
                    )
                } else if (tabIndex == 1 && tts != null) {
                    soundboardViewModel.addTTSButtonToCurrentTemplate(
                        name, tts, selectedColor, selectedIcon, position
                    )
                }
                showDialogAtPosition = null
            },
            onDelete = {},
            initialButton = null,
            soundManagerViewModel = soundManagerViewModel
        )
    }

    /* Editing existent button */
    editingButton?.let { button ->        AddButtonDialog(
        onDismiss = { editingButton = null },
        onConfirm = { name, effect, tts, color, icon, tabIndex ->
            // Decide which update function to call based on the tab
            if (tabIndex == 0 && effect != null) {
                soundboardViewModel.updateSoundEffectButton(button, name, effect, color, icon)
            } else if (tabIndex == 1 && tts != null) {
                soundboardViewModel.updateTTSButton(button, name, tts, color, icon)
            }
            editingButton = null
        },
        onDelete = {
            soundboardViewModel.deleteButtonAtPosition(button.gridPosition)
            editingButton = null
        },
        initialButton = button,
        soundManagerViewModel = soundManagerViewModel
    )
    }
    // Anuncia a TalkBack la entrada y salida del modo edición
    val view = LocalView.current
    val enteredText = stringResource(R.string.edit_mode_banner)
    val exitedText = stringResource(R.string.edit_mode_exited)
    var previousEditMode by remember { mutableStateOf(soundboardViewModel.isEditMode) }
    LaunchedEffect(soundboardViewModel.isEditMode) {
        if (previousEditMode != soundboardViewModel.isEditMode) {
            @Suppress("DEPRECATION")
            view.announceForAccessibility(if (soundboardViewModel.isEditMode) enteredText else exitedText)
            previousEditMode = soundboardViewModel.isEditMode
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (soundboardViewModel.isEditMode) {
            EditModeBanner()
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(soundboardViewModel.columnCount),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
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
                        isEditMode = soundboardViewModel.isEditMode,
                        onClick = {
                            if (soundboardViewModel.isEditMode) {
                                editingButton = buttonAtPosition
                            } else {
                                soundManagerViewModel.playSound(context, buttonAtPosition)
                            }
                        }
                    )
                } else if (soundboardViewModel.isEditMode) {
                    EmptyCellPlaceholder(onClick = { showDialogAtPosition = index })
                }
            }
        }

    }
}

@Composable
fun EditModeBanner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.edit_mode_banner),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
fun SoundButtonItem(
    button: SoundButton,
    isColorblindMode: Boolean,
    isEditMode: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = toSafeColor(Color(button.color))
    val contentColor = getContrastColor(backgroundColor)
    val shape = RoundedCornerShape(8.dp)
    val editLabel = stringResource(R.string.edit_button, button.name)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .combinedClickable(
                onClick = onClick,
                // En modo edición la pulsación larga también abre la edición
                onLongClick = if (isEditMode) onClick else null
            )
            .then(
                if (isEditMode) Modifier.drawWithContent {
                    drawContent()
                    // Borde discontinuo como señal de modo edición
                    val stroke = 3.dp.toPx()
                    drawRoundRect(
                        color = contentColor,
                        topLeft = Offset(stroke / 2, stroke / 2),
                        size = Size(size.width - stroke, size.height - stroke),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        style = Stroke(
                            width = stroke,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                        )
                    )
                } else Modifier
            ),
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = 4.dp,
        border = if (isColorblindMode && !isEditMode) BorderStroke(2.dp, contentColor) else null
    ) {
        Box(contentAlignment = Alignment.Center) {

            if (isEditMode) {
                // Indicador decorativo; el botón completo expone «Editar [nombre]»
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(16.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .then(
                        if (isEditMode) Modifier.clearAndSetSemantics { contentDescription = editLabel }
                        else Modifier
                    )
            ) {
                Icon(
                    imageVector = availableIcons.getOrNull(button.iconRes) ?: Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = button.name,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}



@Preview(name = "Add Button Dialog", showBackground = true)
@Composable
fun AddButtonDialogPreview() {
    val soundboardViewModel: SoundboardViewModel = viewModel()
    val soundManagerViewModel: SoundManagerViewModel = viewModel()
    AddButtonDialog(

        onDismiss = { /* Do nothing */ },
        onConfirm = { name, effect, tts, color, iconRes, tabIndex -> println("Preview: $name with $effect or $tts")},
        onDelete = {},
        initialButton = null,
        soundManagerViewModel = soundManagerViewModel
    )
}



val buttonColors = listOf(
    Color(0xFFEF5350), Color(0xFF66BB6A), Color(0xFF42A5F5),
    Color(0xFFFFCA28), Color(0xFFAB47BC), Color(0xFF26A69A)
)

// Nombres accesibles de SafeColors, en el mismo orden
val safeColorNames = listOf(
    R.string.color_vermilion,
    R.string.color_blue,
    R.string.color_yellow,
    R.string.color_bluish_green,
    R.string.color_orange,
    R.string.color_reddish_purple
)

// Updated availableIcons list for non-verbal communication
val availableIcons = listOf(
    // Essentials / Needs
    Icons.Default.Restaurant,      // Hunger / Eat
    Icons.Default.LocalDrink,      // Thirst / Drink
    Icons.Default.Wc,              // Bathroom
    Icons.Default.Hotel,           // Tired / Sleep
    Icons.Default.MedicalServices, // Pain / Doctor

    // Emotions
    Icons.Default.SentimentVerySatisfied, // Happy / Yes
    Icons.Default.SentimentVeryDissatisfied, // Sad / No / Hurt
    Icons.Default.Warning,                 // Danger / Stop

    // Social / People
    Icons.Default.Handshake,       // Hello / Please
    Icons.Default.PanTool,         // Stop / Wait
    Icons.Default.Favorite,        // Like / Love
    Icons.Default.Home,            // Home
    Icons.Default.Groups,          // Family / Friends

    // Actions / Activities
    Icons.Default.MusicNote,       // Music / Listen
    Icons.Default.PlayArrow,       // Start / Play
    Icons.Default.CheckCircle,     // Done / Good
    Icons.Default.Cancel,          // Bad / End
    Icons.Default.Help,            // Question / I don't know

    // Objects
    Icons.Default.Smartphone,      // Phone / App
    Icons.Default.ShoppingBag      // Buy / Want
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddButtonDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, SoundEffect?, String?, Long, Int, Int) -> Unit,
    onDelete: () -> Unit,
    initialButton: SoundButton? ,
    soundManagerViewModel: SoundManagerViewModel
) {

    var expanded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(initialButton?.name ?: "") }

    var selectedEffect by remember {
        mutableStateOf((initialButton as? SoundEffectButton)?.soundEffect ?: SoundEffect.AWW)
    }
    var ttsText by remember {
        mutableStateOf((initialButton as? TTSButton)?.ttsText ?: "")
    }
    var selectedIconIndex by remember {
        mutableIntStateOf(
            if (initialButton != null && initialButton.iconRes in availableIcons.indices) {
                initialButton.iconRes
            } else {
                0
            }
        )
    }
    var selectedColor by remember {
        mutableStateOf(initialButton?.let { toSafeColor(Color(it.color)) } ?: SafeColors.first())
    }

    var selectedTabIndex by remember {
        mutableIntStateOf(if (initialButton is TTSButton) 1 else 0)
    }

    val tabs = listOf("Botón", "Voz")

    var iconExpanded by remember { mutableStateOf(false) } // State for icon dropdown


    val context = LocalContext.current


    AlertDialog(
        onDismissRequest = onDismiss,

        title = {

            Column {
                Text(if (initialButton == null) "Añadir Botón" else "Editar Botón")
                // TabRow for switching between Button and Speech
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
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

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Botón") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if(selectedTabIndex == 0) {
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedEffect?.displayName ?: "Seleccionar Sonido")
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
                                                    soundManagerViewModel.previewSound(context, effect)
                                                },
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = stringResource(R.string.preview_sound, effect.displayName),
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
                } else {
                    TextField(
                        value = ttsText,
                        onValueChange = { ttsText = it },
                        label = { Text("Texto a decir") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text("Seleccionar Icono", style = MaterialTheme.typography.labelMedium)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { iconExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = availableIcons.getOrNull(selectedIconIndex) ?: Icons.Default.MusicNote,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Elegir Icono")
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

                Text("Elegir Color", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                ) {
                    items(SafeColors.size) { index ->
                        val color = SafeColors[index]
                        val colorName = stringResource(safeColorNames[index])
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .selectable(
                                    selected = selectedColor == color,
                                    role = Role.RadioButton,
                                    onClick = { selectedColor = color }
                                )
                                .semantics { contentDescription = colorName },
                            shape = CircleShape,
                            color = color,
                            border = if (selectedColor == color)
                                BorderStroke(3.dp, MaterialTheme.colorScheme.onSurface) else null
                        ) {}
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val colorLong = selectedColor.toArgb().toLong()

                        if (selectedTabIndex == 0) {
                            // TAB "Botón": We send the sound effect and explicitly set TTS to null
                            onConfirm(
                                name,
                                selectedEffect,
                                null,
                                colorLong,
                                selectedIconIndex,
                                selectedTabIndex
                            )
                        } else {
                            // TAB "Voz": We send the TTS text and set the sound effect to null
                            onConfirm(
                                name,
                                null,
                                ttsText,
                                colorLong,
                                selectedIconIndex,
                                selectedTabIndex
                            )
                        }
                        onDismiss()
                    }
                }
            ) {
                Text(if (initialButton == null) "Añadir" else "Guardar")
            }
        },


        dismissButton = {
            if (initialButton != null) {
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.delete))
                }
            }
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundboardTopBar(soundboardViewModel: SoundboardViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var newTemplateName by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }

    if (showRenameDialog) {
        val template = soundboardViewModel.templates.getOrNull(soundboardViewModel.currentTemplateIndex)
        var renamedName by remember { mutableStateOf(template?.name ?: "") }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text(stringResource(R.string.rename_template_title)) },
            text = {
                TextField(
                    value = renamedName,
                    onValueChange = { renamedName = it },
                    label = { Text("Nombre de la Plantilla") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (renamedName.isNotBlank()) {
                            soundboardViewModel.renameCurrentTemplate(renamedName.trim())
                            showRenameDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { 
                showDialog = false
                newTemplateName = ""
            },
            title = { Text("Nueva Plantilla") },
            text = {
                TextField(
                    value = newTemplateName,
                    onValueChange = { newTemplateName = it },
                    label = { Text("Nombre de la Plantilla") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTemplateName.isNotBlank()) {
                            soundboardViewModel.addTemplate(newTemplateName)
                            showDialog = false
                            newTemplateName = ""
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF2E7D32))
                ) {
                    Text("Añadir")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDialog = false
                    newTemplateName = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Borrar Plantilla") },
            text = { Text("¿Estás seguro de que quieres borrar esta plantilla?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        soundboardViewModel.deleteCurrentTemplate()
                        soundboardViewModel.toggleEditMode()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column {
        TopAppBar(
            navigationIcon = {
                // No se puede crear otra plantilla mientras se edita una
                if (!soundboardViewModel.isEditMode) {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir Plantilla")
                    }
                }
            },
            title = {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    if (soundboardViewModel.isEditMode) {
                        val template = soundboardViewModel.templates.getOrNull(soundboardViewModel.currentTemplateIndex)
                        if (template != null) {
                            item {
                                FilterChip(
                                    selected = true,
                                    onClick = { },
                                    label = { Text(template.name) }
                                )
                            }
                        }
                    } else {
                        itemsIndexed(soundboardViewModel.templates) { index, template ->
                            FilterChip(
                                selected = soundboardViewModel.currentTemplateIndex == index,
                                onClick = { soundboardViewModel.currentTemplateIndex = index },
                                label = { Text(template.name) }
                            )
                        }
                    }
                }
            },
            actions = {
                if (!soundboardViewModel.isEditMode) {
                    if(soundboardViewModel.templates.isNotEmpty()) {
                        TextButton(
                            onClick = { soundboardViewModel.toggleEditMode() }
                        ) {
                            Text("Editar")
                        }
                    }
                }
            }
        )

        // Acciones de la plantilla en edición, con texto que explica cada una
        if (soundboardViewModel.isEditMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showRenameDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.rename_template), textAlign = TextAlign.Center)
                }
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.delete_template), textAlign = TextAlign.Center)
                }
            }
            Button(
                onClick = { soundboardViewModel.toggleEditMode() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.apply_changes))
            }
        }
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
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Añadir Botón")
    }
}