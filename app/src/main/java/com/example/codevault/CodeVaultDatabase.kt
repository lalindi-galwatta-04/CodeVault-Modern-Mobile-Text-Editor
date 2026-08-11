package com.example.codevault

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [VersionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CodeVaultDatabase : RoomDatabase() {

    abstract fun versionDao(): VersionDao

    companion object {

        @Volatile
        private var INSTANCE: CodeVaultDatabase? = null

        fun getDatabase(context: Context): CodeVaultDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CodeVaultDatabase::class.java,
                    "codevault_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}