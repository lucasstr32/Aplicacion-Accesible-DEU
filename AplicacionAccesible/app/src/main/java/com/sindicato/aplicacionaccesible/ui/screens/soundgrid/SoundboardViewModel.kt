package com.sindicato.aplicacionaccesible.ui.screens.soundgrid

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SoundboardViewModel {

    private val _templates = mutableStateListOf<Template>()
    val templates: List<Template> = _templates

    var currentTemplateIndex by mutableIntStateOf(0) // actual template
    var isEditMode by mutableStateOf(false) // indica si estamos en modo edición
    var columnCount by mutableIntStateOf(2) // cantidad de columnas


    init{
        var defaultButtons = listOf(
            SoundButton("Ayuda", 0),
            SoundButton("Aviso", 1),
            SoundButton("Aplausos", 2),
            SoundButton("Advertencia", 3),
            SoundButton("Beso", 4)
        )
        _templates.add(Template("Default", defaultButtons))

        var defaultButtons2 = listOf(
            SoundButton("A", 0),
            SoundButton("B", 1),
            SoundButton("C", 2),
            SoundButton("D", 3),
            SoundButton("E", 4),
            SoundButton("F", 5),
            SoundButton("G", 6),
            SoundButton("H", 7),
            SoundButton("I", 8),
            SoundButton("J", 9),
            SoundButton("K", 10),
            SoundButton("L", 11),
            SoundButton("M", 12),
            SoundButton("N", 13),
            SoundButton("O", 14)
        )

        _templates.add(Template("Alfabeto", defaultButtons2))
    }

    fun addTemplate(name: String) {
        _templates.add(Template(name = name))
        currentTemplateIndex = _templates.size - 1
    }

    fun deleteCurrentTemplate(){
        if(_templates.isNotEmpty()){
            _templates.removeAt(currentTemplateIndex)
            if(currentTemplateIndex >= _templates.size){
                currentTemplateIndex = _templates.size - 1
            }
        }
    }

    fun updateButton(templateId: String, button: SoundButton){
        // Lógica para reemplazar o añadir boton en una posición de la grilla
    }

    fun getIsEditMode(): Boolean{
        return isEditMode
    }

    fun toggleEditMode(){
        isEditMode = !isEditMode
    }
}