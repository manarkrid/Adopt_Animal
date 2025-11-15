package com.example.adoptapet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Query("SELECT * FROM animals")
    fun getAllAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE type = :type")
    fun getAnimalsByType(type: String): Flow<List<AnimalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(animals: List<AnimalEntity>)

    @Query("DELETE FROM animals")
    suspend fun deleteAllAnimals()

    @Query("SELECT COUNT(*) FROM animals")
    suspend fun getAnimalsCount(): Int
}
