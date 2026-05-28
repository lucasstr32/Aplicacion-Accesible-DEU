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