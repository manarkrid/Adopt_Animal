package com.example.adoptapet.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.adoptapet.model.Animal
import com.example.adoptapet.model.AnimalType

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String,
    val imageRes: Int,
    val imageUrl: String?,
    val type: String
) {
    fun toAnimal(): Animal {
        return Animal(
            id = id,
            name = name,
            description = description,
            imageRes = imageRes,
            imageUrl = imageUrl,
            type = AnimalType.valueOf(type)
        )
    }

    companion object {
        fun fromAnimal(animal: Animal): AnimalEntity {
            return AnimalEntity(
                id = animal.id,
                name = animal.name,
                description = animal.description,
                imageRes = animal.imageRes,
                imageUrl = animal.imageUrl,
                type = animal.type.name
            )
        }
    }
}
