package com.sindicato.aplicacionaccesible.data

import androidx.compose.runtime.snapshots.toInt
import com.sindicato.aplicacionaccesible.data.dao.ButtonDao
import com.sindicato.aplicacionaccesible.data.dao.TemplateDao
import com.sindicato.aplicacionaccesible.data.entity.ButtonEntity
import com.sindicato.aplicacionaccesible.data.entity.TemplateEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object DatabaseSeeder {
    suspend fun seedDatabase(
        signLanguageDao: SignLanguageDao,
        templateDao: TemplateDao,
        buttonsDao: ButtonDao
    ) {
        withContext(Dispatchers.IO) {
            if (signLanguageDao.getItemCount() == 0) {
                val initialData = listOf(
                    SignLanguageEntity(word = "Hola", imageName = "hola"),
                    SignLanguageEntity(word = "Yo", imageName = "yo"),
                    SignLanguageEntity(word = "Gracias", imageName = "gracias"),
                    SignLanguageEntity(word = "De nada", imageName = "denada"),
                    SignLanguageEntity(word = "Chau", imageName = "chau"),
                    SignLanguageEntity(word = "Saludar", imageName = "saludar"),
                    SignLanguageEntity(word = "Nombre", imageName = "nombre"),
                    SignLanguageEntity(word = "Apellido", imageName = "apellido"),
                    SignLanguageEntity(word = "Interprete", imageName = "interprete"),
                    SignLanguageEntity(word = "Oyente", imageName = "oyente"),
                    SignLanguageEntity(word = "Buenos Días", imageName = "buenosdias"),
                    SignLanguageEntity(word = "Buenas Tardes", imageName = "buenastardes"),
                    SignLanguageEntity(word = "Buenas Noches", imageName = "buenasnoches"),
                    SignLanguageEntity(word = "Sordo/a", imageName = "sordo")
                )
                signLanguageDao.insertAll(initialData)
            }
            if (templateDao.getTemplateCount() == 0) {
                // Crear primera plantilla: "Básico"
                val basicTemplate = TemplateEntity(
                    name = "Básico",
                    id = UUID.randomUUID().toString()
                )
                templateDao.insertTemplate(basicTemplate)

                val basicButtons = listOf(
                    ButtonEntity(
                        templateId = basicTemplate.id,
                        name = "Hola",
                        gridPosition = 0,
                        soundEffect = null, // Usará TTS
                        ttsText = "Hola, ¿cómo estás?",
                        color = 0xFF009E73, // SafeGreen
                        iconRes = 5 // Face
                    ),
                    ButtonEntity(
                        templateId = basicTemplate.id,
                        name = "Aplauso",
                        gridPosition = 1,
                        soundEffect = "CLAP", // Usará sonido físico
                        ttsText = null,
                        color = 0xFF0072B2, // SafeBlue
                        iconRes = 0 // Music
                    )
                )

                // Crear segunda plantilla: "Emergencia"
                val emergencyTemplate = TemplateEntity(
                    name = "Emergencia",
                    id = UUID.randomUUID().toString()
                )
                templateDao.insertTemplate(emergencyTemplate)

                val emergencyButtons = listOf(
                    ButtonEntity(
                        templateId = emergencyTemplate.id,
                        name = "Ayuda",
                        gridPosition = 0,
                        soundEffect = null,
                        ttsText = "Necesito ayuda, por favor",
                        color = 0xFFD55E00, // SafeRed
                        iconRes = 4 // Warning
                    ),
                    ButtonEntity(
                        templateId = emergencyTemplate.id,
                        name = "Alarma",
                        gridPosition = 1,
                        soundEffect = "BOMB",
                        ttsText = null,
                        color = 0xFFE69F00, // SafeOrange
                        iconRes = 1 // Notifications
                    )
                )

                // Insertar todos los botones en la base de datos
                buttonsDao.insertButtonsList(basicButtons + emergencyButtons)
            }
        }
    }
}
