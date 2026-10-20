package com.sindicato.aplicacionaccesible.viewmodel

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sindicato.aplicacionaccesible.data.AppDatabase
import com.sindicato.aplicacionaccesible.data.repository.ButtonRepository
import com.sindicato.aplicacionaccesible.data.repository.TemplateRepository
import com.sindicato.aplicacionaccesible.data.settings.SettingsRepository
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffectButton
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.TTSButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SoundboardViewModelTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    private lateinit var db: AppDatabase
    private lateinit var dataStoreScope: CoroutineScope
    private lateinit var viewModel: SoundboardViewModel

    @Before
    fun setUp() {
        // Base en memoria y DataStore temporal: no toca los datos reales de la app
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val settingsFile = File(context.cacheDir, "test-${UUID.randomUUID()}.preferences_pb")
        dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val settings = SettingsRepository(
            PreferenceDataStoreFactory.create(scope = dataStoreScope) { settingsFile }
        )
        onMain {
            viewModel = SoundboardViewModel(
                TemplateRepository(db.templateDao()),
                ButtonRepository(db.buttonDao()),
                settings
            )
        }
        waitUntil { viewModel.templatesLoaded }
    }

    @After
    fun tearDown() {
        db.close()
        dataStoreScope.cancel()
        // Borra todos los DataStore temporales creados por los tests
        context.cacheDir.listFiles { file -> file.name.startsWith("test-") }?.forEach { it.delete() }
    }

    private fun onMain(block: () -> Unit) = instrumentation.runOnMainSync(block)

    /** Espera a que las escrituras asíncronas del ViewModel lleguen a la base. */
    private fun waitUntil(condition: suspend () -> Boolean) = runBlocking {
        val deadline = System.currentTimeMillis() + 5_000
        while (!condition()) {
            check(System.currentTimeMillis() < deadline) { "La condición no se cumplió a tiempo" }
            Thread.sleep(50)
        }
    }

    private fun dbTemplates() = runBlocking { db.templateDao().getAllTemplatesWithButtons() }

    private fun addTemplate(name: String) {
        onMain { viewModel.addTemplate(name) }
        waitUntil { db.templateDao().getTemplateCount() == 1 }
    }

    @Test
    fun templateAddedDuringInitialLoadIsKept() {
        // Sin esperar la carga inicial: antes la carga borraba la plantilla recién creada
        lateinit var fresh: SoundboardViewModel
        onMain {
            fresh = SoundboardViewModel(
                TemplateRepository(db.templateDao()),
                ButtonRepository(db.buttonDao()),
                SettingsRepository(PreferenceDataStoreFactory.create(scope = dataStoreScope) {
                    File(context.cacheDir, "test-${UUID.randomUUID()}.preferences_pb")
                })
            )
            fresh.addTemplate("Rápida")
        }
        waitUntil { fresh.templatesLoaded }

        assertEquals(listOf("Rápida"), fresh.templates.map { it.name })
    }

    @Test
    fun addTemplatePersistsAndEntersEditMode() {
        addTemplate("Casa")

        assertEquals("Casa", dbTemplates().single().template.name)
        assertTrue(viewModel.isEditMode)
    }

    @Test
    fun addEditAndDeleteButtonArePersisted() {
        addTemplate("Casa")

        onMain { viewModel.addTTSButtonToCurrentTemplate("Hola", "Hola a todos", 0xFF0072B2, 0, 3) }
        waitUntil { db.buttonDao().getButtonsByTemplateId(dbTemplates().single().template.id).size == 1 }

        val button = viewModel.templates.single().buttons.single()
        onMain { viewModel.updateSoundEffectButton(button, "Timbre", SoundEffect.DOORBELL, 0xFFD55E00, 2) }
        waitUntil {
            db.buttonDao().getButtonsByTemplateId(dbTemplates().single().template.id).single().name == "Timbre"
        }
        val updated = viewModel.templates.single().buttons.single()
        assertTrue(updated is SoundEffectButton)
        assertEquals(SoundEffect.DOORBELL, (updated as SoundEffectButton).soundEffect)

        onMain { viewModel.deleteButtonAtPosition(3) }
        waitUntil { db.buttonDao().getButtonsByTemplateId(dbTemplates().single().template.id).isEmpty() }
        assertTrue(viewModel.templates.single().buttons.isEmpty())
    }

    @Test
    fun addingButtonOnOccupiedCellReplacesIt() {
        addTemplate("Casa")

        onMain { viewModel.addTTSButtonToCurrentTemplate("Hola", "Hola", 0xFF0072B2, 0, 0) }
        onMain { viewModel.addTTSButtonToCurrentTemplate("Chau", "Chau", 0xFF0072B2, 0, 0) }
        waitUntil {
            db.buttonDao().getButtonsByTemplateId(dbTemplates().single().template.id).map { it.name } == listOf("Chau")
        }

        assertEquals(listOf("Chau"), viewModel.templates.single().buttons.map { (it as TTSButton).name })
    }

    @Test
    fun renameAndDeleteTemplateArePersisted() {
        addTemplate("Casa")

        onMain { viewModel.renameCurrentTemplate("Hogar") }
        waitUntil { dbTemplates().single().template.name == "Hogar" }
        assertEquals("Hogar", viewModel.templates.single().name)

        onMain { viewModel.deleteCurrentTemplate() }
        waitUntil { db.templateDao().getTemplateCount() == 0 }
        assertTrue(viewModel.templates.isEmpty())
    }
}
