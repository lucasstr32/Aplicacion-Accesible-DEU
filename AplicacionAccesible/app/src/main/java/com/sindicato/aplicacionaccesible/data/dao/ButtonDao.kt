package com.sindicato.aplicacionaccesible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sindicato.aplicacionaccesible.data.entity.ButtonEntity

@Dao
interface ButtonDao {

    @Query("SELECT * FROM buttons WHERE templateId = :templateId")
    fun getButtonsByTemplateId(templateId: Int): List<ButtonEntity>

    @Insert
    suspend fun insertButton(button: ButtonEntity)

}