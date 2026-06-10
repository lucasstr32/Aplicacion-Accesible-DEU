package com.sindicato.aplicacionaccesible.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "buttons",
    foreignKeys = [
        ForeignKey(
            entity = TemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ButtonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val templateId: String,
    val name: String,
    val gridPosition: Int,
    val soundEffect: String,
    val ttsText: String? = null,
    val color: Long,
    val iconRes: Int
)