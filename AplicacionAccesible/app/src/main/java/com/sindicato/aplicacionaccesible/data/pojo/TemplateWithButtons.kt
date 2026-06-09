package com.sindicato.aplicacionaccesible.data.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.sindicato.aplicacionaccesible.data.entity.ButtonEntity
import com.sindicato.aplicacionaccesible.data.entity.TemplateEntity

data class TemplateWithButtons(
    @Embedded val template: TemplateEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "templateId"
    )
    val buttons: List<ButtonEntity>
)
