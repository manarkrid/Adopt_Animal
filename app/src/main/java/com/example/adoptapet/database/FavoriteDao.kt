package com.example.adoptapet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE animalId = :animalId")
    suspend fun deleteFavorite(animalId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE animalId = :animalId)")
    suspend fun isFavorite(animalId: Int): Boolean
}
