package com.sindicato.aplicacionaccesible.data.repository

import com.sindicato.aplicacionaccesible.data.dao.ButtonDao
import com.sindicato.aplicacionaccesible.data.entity.ButtonEntity
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect

class ButtonRepository(
    private val buttonDao: ButtonDao

) {
    suspend fun getButtonsByTemplateId(templateId: String): List<SoundButton> {

        val entities = buttonDao.getButtonsByTemplateId(templateId)
        return entities.map { entity ->
            SoundButton(
                name = entity.name,
                gridPosition = entity.gridPosition,
                soundEffect = SoundEffect.fromDisplayName(entity.name),
                color = entity.color,
                iconRes = entity.iconRes
            )
        }
    }


    suspend fun insertButton(templateId: String, button: SoundButton){
        val entity = ButtonEntity(
            templateId = templateId,
            name = button.name,
            gridPosition = button.gridPosition,
            soundEffect = button.soundEffect.toString(), // Assuming you store the Enum ordinal
            color = button.color,
            iconRes = button.iconRes,
            ttsText = button.ttsText,
        )
        buttonDao.insertButton(entity)

    }



    suspend fun deleteButton(templateId: String, gridPosition: Int){
        buttonDao.deleteButton(templateId, gridPosition)
    }


    suspend fun updateButton(templateId: String, oldButton: SoundButton, newButton: SoundButton){
        buttonDao.updateButton(templateId, oldButton.gridPosition, newButton.name,
            newButton.soundEffect.toString(), newButton.ttsText, newButton.color, newButton.iconRes)
    }
}