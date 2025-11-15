package com.example.adoptapet.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adoptions")
data class AdoptionEntity(
    @PrimaryKey
    val animalId: Int
)
