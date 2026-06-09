package com.sindicato.aplicacionaccesible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sindicato.aplicacionaccesible.data.entity.TemplateEntity
import com.sindicato.aplicacionaccesible.data.pojo.TemplateWithButtons
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {

    @Transaction
    @Query("SELECT * FROM templates")
    fun getAllTemplatesWithButtons(): Flow<List<TemplateWithButtons>>
    @Query("SELECT * FROM templates WHERE id = :templateId")
    fun getTemplateById(templateId: Int): TemplateWithButtons?

    @Insert
    suspend fun insertTemplate(template: TemplateEntity)
}