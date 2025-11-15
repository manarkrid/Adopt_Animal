package com.example.adoptapet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AnimalEntity::class, FavoriteEntity::class, AdoptionEntity::class], 
    version = 2, 
    exportSchema = false
)
abstract class AnimalDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun adoptionDao(): AdoptionDao

    companion object {
        @Volatile
        private var INSTANCE: AnimalDatabase? = null

        fun getDatabase(context: Context): AnimalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnimalDatabase::class.java,
                    "animal_database"
                )
                .fallbackToDestructiveMigration() // Pour la migration de v1 à v2
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
