package com.sindicato.aplicacionaccesible.data.repository

import com.sindicato.aplicacionaccesible.data.dao.TemplateDao
import com.sindicato.aplicacionaccesible.data.entity.TemplateEntity
import com.sindicato.aplicacionaccesible.data.pojo.TemplateWithButtons
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffectButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.TTSButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.Template

class TemplateRepository(
    private val templateDao: TemplateDao
) {

    suspend fun getTemplateById(templateId: Int): Template? {
        val relation: TemplateWithButtons = templateDao.getTemplateById(templateId) ?: return null

        val uiButtons = relation.buttons.map { entity ->
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

        return Template(
            id = relation.template.id,
            name = relation.template.name, // relation.template is the TemplateEntity
            buttons = uiButtons,

        )
    }

    suspend fun insertTemplate(template: Template) {
        val entity = TemplateEntity(id = template.id, name = template.name)
        templateDao.insertTemplate(entity)

    }

    suspend fun getAllTemplates(): List<Template> {
        val templatesWithButtons = templateDao.getAllTemplatesWithButtons()

        return templatesWithButtons.map { relation ->
            Template(
                id = relation.template.id.toString(), // Ensure ID is mapped
                name = relation.template.name,
                buttons = relation.buttons.map { btn ->
                    if(btn.soundEffect.isNullOrEmpty() && !btn.ttsText.isNullOrEmpty()){
                        TTSButton(
                            name = btn.name,
                            gridPosition = btn.gridPosition,
                            ttsText = btn.ttsText,
                            color = btn.color,
                            iconRes = btn.iconRes
                        )
                    }
                    else {
                        val effect: String = try {
                            SoundEffect.valueOf(btn.soundEffect ?: "KISS").name
                        } catch (e: Exception) {
                            SoundEffect.AWW.name
                        }
                        SoundEffectButton(
                            name = btn.name,
                            gridPosition = btn.gridPosition,
                            soundEffect = SoundEffect.valueOf(effect),
                            color = btn.color,
                            iconRes = btn.iconRes
                        )
                    }
                }
            )
        }
    }

    suspend fun deleteTemplateFromDb(templateId: String){
        templateDao.deleteTemplateById(templateId)

    }

}