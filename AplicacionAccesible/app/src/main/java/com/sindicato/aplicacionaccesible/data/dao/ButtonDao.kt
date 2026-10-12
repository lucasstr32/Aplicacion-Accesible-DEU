package com.sindicato.aplicacionaccesible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sindicato.aplicacionaccesible.data.entity.ButtonEntity

@Dao
interface ButtonDao {

    @Query("SELECT * FROM buttons WHERE templateId = :templateId")
    suspend fun getButtonsByTemplateId(templateId: String): List<ButtonEntity>

    @Insert
    suspend fun insertButton(button: ButtonEntity)

    @Query("DELETE FROM buttons WHERE templateId = :templateId AND gridPosition = :gridPosition")
    suspend fun deleteButton(templateId: String, gridPosition: Int)

    @Query("UPDATE buttons SET " +
            "name = :newName, " +
            "soundEffect = :newEffect, " +
            "ttsText = :newTtsText, "+
            "color = :newColor, " +
            "iconRes = :newIcon " +
            "WHERE templateId = :templateId AND gridPosition = :gridPosition")
    suspend fun updateButton(templateId: String, gridPosition: Int, newName: String,
                             newEffect: String, newTtsText: String?, newColor: Long, newIcon: Int)


    /**
     * Reemplaza el botón de una celda: evita filas duplicadas en la misma posición.
     */
    @Transaction
    suspend fun replaceButton(button: ButtonEntity) {
        deleteButton(button.templateId, button.gridPosition)
        insertButton(button)
    }

    @Insert
    suspend fun insertButtonsList(buttons: List<ButtonEntity>)
}