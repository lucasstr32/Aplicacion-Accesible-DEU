package com.sindicato.aplicacionaccesible.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sindicato.aplicacionaccesible.data.dao.ButtonDao
import com.sindicato.aplicacionaccesible.data.dao.TemplateDao
import com.sindicato.aplicacionaccesible.data.entity.ButtonEntity
import com.sindicato.aplicacionaccesible.data.entity.TemplateEntity

@Database(
    entities =
        [
            SignLanguageEntity::class,
            PhraseEntity::class,
            TemplateEntity::class,
            ButtonEntity::class
        ],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun signLanguageDao(): SignLanguageDao
    abstract fun phraseDao(): PhraseDao

    abstract fun templateDao(): TemplateDao
    abstract fun buttonDao(): ButtonDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // La versión 5 es la línea base. Las anteriores (1-4) sólo existieron
                    // en desarrollo y se pueden recrear sin migración.
                    //
                    // Para el próximo cambio de esquema:
                    //  1. Subir `version` a 6 y compilar: Room genera app/schemas/.../6.json.
                    //  2. Escribir `val MIGRATION_5_6 = object : Migration(5, 6) { ... }`
                    //     con el SQL del cambio y agregarlo con `.addMigrations(MIGRATION_5_6)`.
                    //  3. Agregar `androidx.room:room-testing` y un test con
                    //     MigrationTestHelper que valide la migración 5 → 6.
                    // Nunca volver a usar fallbackToDestructiveMigration(): borraría las
                    // plantillas y frases de las personas al actualizar la app.
                    .fallbackToDestructiveMigrationFrom(1, 2, 3, 4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
