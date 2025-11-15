package com.example.adoptapet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdoptionDao {
    @Query("SELECT * FROM adoptions")
    fun getAllAdoptions(): Flow<List<AdoptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdoption(adoption: AdoptionEntity)

    @Query("DELETE FROM adoptions WHERE animalId = :animalId")
    suspend fun deleteAdoption(animalId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM adoptions WHERE animalId = :animalId)")
    suspend fun isAdopted(animalId: Int): Boolean
}
