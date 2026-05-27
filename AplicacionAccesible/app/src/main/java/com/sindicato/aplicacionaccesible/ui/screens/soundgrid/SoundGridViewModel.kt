package com.sindicato.aplicacionaccesible.ui.screens.soundgrid

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SoundGridViewModel {

    //
    private val _templates = mutableListOf<Template>()
    val templates: List<Template> = _templates

    var currentTemplateIndex by mutableIntStateOf(0) // actual template
    var idEditMode by mutableStateOf(false) // indica si estamos en modo edición
    var columnCount by mutableIntStateOf(2) // cantidad de columnas

    fun addTempalte(name: String){
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


}