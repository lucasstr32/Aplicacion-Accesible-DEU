package com.sindicato.aplicacionaccesible.data.repository

import com.sindicato.aplicacionaccesible.data.dao.TemplateDao
import com.sindicato.aplicacionaccesible.data.entity.TemplateEntity
import com.sindicato.aplicacionaccesible.data.pojo.TemplateWithButtons
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.Template

class TemplateRepository(
    private val templateDao: TemplateDao
) {

    suspend fun getTemplateById(templateId: Int): Template? {
        val relation: TemplateWithButtons = templateDao.getTemplateById(templateId) ?: return null

        val uiButtons = relation.buttons.map { entity ->
            SoundButton(
                name = entity.name,
                gridPosition = entity.gridPosition,
                soundEffect = SoundEffect.valueOf(entity.soundEffect.toString()), // Assuming you store the Enum name as String
                color = entity.color,
                iconRes = entity.iconRes
            )
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

}