package com.sindicato.aplicacionaccesible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sindicato.aplicacionaccesible.data.entity.ButtonEntity

@Dao
interface ButtonDao {

    @Query("SELECT * FROM buttons WHERE templateId = :templateId")
    suspend fun getButtonsByTemplateId(templateId: String): List<ButtonEntity>

    @Insert
    suspend fun insertButton(button: ButtonEntity)

    @Query("DELETE FROM buttons WHERE templateId = :templateId AND name = :buttonName")
    suspend fun deleteButton(templateId: String, buttonName: String)

}