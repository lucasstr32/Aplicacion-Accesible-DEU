package com.sindicato.aplicacionaccesible.data.repository

import com.sindicato.aplicacionaccesible.data.dao.ButtonDao
import com.sindicato.aplicacionaccesible.data.entity.ButtonEntity
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffectButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.TTSButton

class ButtonRepository(
    private val buttonDao: ButtonDao

) {
    suspend fun getButtonsByTemplateId(templateId: String): List<SoundButton> {

        val entities = buttonDao.getButtonsByTemplateId(templateId)
        return entities.map { entity ->

            /* based on which (soundEffect or ttsText) is null
            * the corresponding button is created
            * */
            if(entity.soundEffect.isNullOrEmpty() && !entity.ttsText.isNullOrEmpty()){
                TTSButton(
                    name = entity.name,
                    gridPosition = entity.gridPosition,
                    ttsText = entity.ttsText,
                    color = entity.color,
                    iconRes = entity.iconRes
                )
            }
            else {
                val effect: String = try {
                    SoundEffect.valueOf(entity.soundEffect ?: "KISS").name
                } catch (e: Exception) {
                    SoundEffect.AWW.name
                }
                SoundEffectButton(
                    name = entity.name,
                    gridPosition = entity.gridPosition,
                    soundEffect = SoundEffect.valueOf(effect),
                    color = entity.color,
                    iconRes = entity.iconRes
                )
            }

        }
    }


    suspend fun insertButton(templateId: String, button: SoundButton) {
        val entity = ButtonEntity(
            templateId = templateId,
            name = button.name,
            gridPosition = button.gridPosition,
            color = button.color,
            iconRes = button.iconRes,
            // Cast to access specific subclass properties
            soundEffect = (button as? SoundEffectButton)?.soundEffect?.name,
            ttsText = (button as? TTSButton)?.ttsText
        )
        buttonDao.insertButton(entity)
    }



    suspend fun deleteButton(templateId: String, gridPosition: Int){
        buttonDao.deleteButton(templateId, gridPosition)
    }


    suspend fun updateButton(templateId: String, oldPosition: Int, newButton: SoundButton) {
        // Extract subclass properties safely
        val soundEffectStr = (newButton as? SoundEffectButton)?.soundEffect?.name
        val ttsText = (newButton as? TTSButton)?.ttsText

        buttonDao.updateButton(
            templateId = templateId,
            gridPosition = oldPosition,
            newName = newButton.name,
            newEffect = soundEffectStr ?: "",
            newTtsText = ttsText ?: "",
            newColor = newButton.color,
            newIcon = newButton.iconRes
        )
    }
}