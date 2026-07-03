package com.sindicato.aplicacionaccesible.data

import androidx.compose.runtime.snapshots.toInt
import com.sindicato.aplicacionaccesible.data.dao.ButtonDao
import com.sindicato.aplicacionaccesible.data.dao.TemplateDao
import com.sindicato.aplicacionaccesible.data.entity.ButtonEntity
import com.sindicato.aplicacionaccesible.data.entity.TemplateEntity
import com.sindicato.aplicacionaccesible.ui.screens.soundgrid.SoundEffect
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
                val afueraTemplate = TemplateEntity(
                    name = "Afuera",
                    id = UUID.randomUUID().toString()
                )
                templateDao.insertTemplate(afueraTemplate)

                val basicButtons = listOf(
                    ButtonEntity(
                        templateId = afueraTemplate.id,
                        name = "Permiso",
                        gridPosition = 0,
                        soundEffect = null, // Usará TTS
                        ttsText = "Permiso",
                        color = 0xFF009E73, // SafeGreen
                        iconRes = 5 // Face
                    ),
                    ButtonEntity(
                        templateId = afueraTemplate.id,
                        name = "Ouch",
                        gridPosition = 1,
                        soundEffect = SoundEffect.MALE_OUCH.name, // Usará sonido físico
                        ttsText = null,
                        color = 0xFF0072B2, // SafeBlue
                        iconRes = 0 // Music
                    ),
                    ButtonEntity(
                        templateId = afueraTemplate.id,
                        name = "Si",
                        gridPosition = 2,
                        soundEffect = null,
                        ttsText = "Si",
                        color = 0xFF009E73, // SafeGreen
                        iconRes = 0 // Music
                    ),
                    ButtonEntity(
                        templateId = afueraTemplate.id,
                        name = "No",
                        gridPosition = 3,
                        soundEffect = null,
                        ttsText = "No",
                        color = 0xFF009E73, // SafeGreen
                        iconRes = 0
                    ),
                    ButtonEntity(
                        templateId = afueraTemplate.id,
                        name = "Gracias",
                        gridPosition = 4,
                        soundEffect = null,
                        ttsText = "Gracias",
                        color = 0xFF009E73, // SafeGreen
                        iconRes = 0
                    )
                )

                // Crear segunda plantilla: "Emergencia"
                val facultadTemplate = TemplateEntity(
                    name = "Facultad",
                    id = UUID.randomUUID().toString()
                )
                templateDao.insertTemplate(facultadTemplate)

                val emergencyButtons = listOf(
                    ButtonEntity(
                        templateId = facultadTemplate.id,
                        name = "No entendi",
                        gridPosition = 0,
                        soundEffect = null,
                        ttsText = "No entendí, ¿Podrías repetir?",
                        color = 0xFFD55E00, // SafeRed
                        iconRes = 4 // Warning
                    ),
                    ButtonEntity(
                        templateId = facultadTemplate.id,
                        name = "Necesito ayuda",
                        gridPosition = 1,
                        soundEffect = null,
                        ttsText = "Necesito ayuda",
                        color = 0xFFE69F00, // SafeOrange
                        iconRes = 1 // Notifications
                    ),
                    ButtonEntity(
                        templateId = facultadTemplate.id,
                        name = "¿Puedo ir al baño?",
                        gridPosition = 2,
                        soundEffect = null,
                        ttsText = "¿Puedo ir al baño?",
                        color = 0xFFE69F00, // SafeOrange
                        iconRes = 0 // Music
                    ),
                    ButtonEntity(
                        templateId = facultadTemplate.id,
                        name = "Pensando",
                        gridPosition = 3,
                        soundEffect = SoundEffect.MALE_THINKING.name,
                        ttsText = null,
                        color = 0xFF009E73, // SafeGreen
                        iconRes = 0 // Music
                    )
                )

                // Insertar todos los botones en la base de datos
                buttonsDao.insertButtonsList(basicButtons + emergencyButtons)
            }
        }
    }
}
