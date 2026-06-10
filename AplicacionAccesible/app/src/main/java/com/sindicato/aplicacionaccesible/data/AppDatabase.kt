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
    version = 4,
    exportSchema = false
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
