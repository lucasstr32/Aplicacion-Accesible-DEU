package com.sindicato.aplicacionaccesible.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sindicato.aplicacionaccesible.data.entity.ButtonEntity
import com.sindicato.aplicacionaccesible.data.entity.TemplateEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DaoTest {

    private lateinit var db: AppDatabase

    private val template = TemplateEntity(id = "t1", name = "Casa")

    private fun button(position: Int, name: String) = ButtonEntity(
        templateId = template.id,
        name = name,
        gridPosition = position,
        ttsText = name,
        color = 0xFF0072B2,
        iconRes = 0
    )

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Base en memoria: no toca los datos reales de la app
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        runBlocking { db.templateDao().insertTemplate(template) }
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun templatesAreReadWithTheirButtons() = runBlocking {
        db.buttonDao().insertButtonsList(listOf(button(0, "Hola"), button(1, "Chau")))

        val templates = db.templateDao().getAllTemplatesWithButtons()

        assertEquals(1, templates.size)
        assertEquals(setOf("Hola", "Chau"), templates[0].buttons.map { it.name }.toSet())
    }

    @Test
    fun replaceButtonKeepsOneButtonPerCell() = runBlocking {
        db.buttonDao().replaceButton(button(0, "Hola"))
        db.buttonDao().replaceButton(button(0, "Buenas"))

        val buttons = db.buttonDao().getButtonsByTemplateId(template.id)

        assertEquals(listOf("Buenas"), buttons.map { it.name })
    }

    @Test
    fun deleteButtonRemovesOnlyThatCell() = runBlocking {
        db.buttonDao().insertButtonsList(listOf(button(0, "Hola"), button(1, "Chau")))

        db.buttonDao().deleteButton(template.id, 0)

        assertEquals(listOf(1), db.buttonDao().getButtonsByTemplateId(template.id).map { it.gridPosition })
    }

    @Test
    fun updateButtonChangesFields() = runBlocking {
        db.buttonDao().insertButton(button(0, "Hola"))

        db.buttonDao().updateButton(template.id, 0, "Ayuda", "", "Necesito ayuda", 0xFFD55E00, 7)

        val updated = db.buttonDao().getButtonsByTemplateId(template.id).single()
        assertEquals("Ayuda", updated.name)
        assertEquals("Necesito ayuda", updated.ttsText)
        assertEquals(7, updated.iconRes)
    }

    @Test
    fun renameTemplateChangesName() = runBlocking {
        db.templateDao().renameTemplate(template.id, "Hogar")

        assertEquals("Hogar", db.templateDao().getAllTemplatesWithButtons().single().template.name)
    }

    @Test
    fun deletingTemplateDeletesItsButtons() = runBlocking {
        db.buttonDao().insertButton(button(0, "Hola"))

        db.templateDao().deleteTemplateById(template.id)

        assertEquals(0, db.templateDao().getTemplateCount())
        assertTrue(db.buttonDao().getButtonsByTemplateId(template.id).isEmpty())
    }

    @Test
    fun phrasesAreSavedAndDeleted() = runBlocking {
        db.phraseDao().insertPhrase(PhraseEntity(text = "Hola"))
        val saved = db.phraseDao().getAllPhrases().first().single()
        assertEquals("Hola", saved.text)

        db.phraseDao().deletePhrase(saved)

        assertTrue(db.phraseDao().getAllPhrases().first().isEmpty())
    }
}
