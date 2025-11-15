package com.example.adoptapet

import androidx.compose.runtime.mutableStateMapOf
import com.example.adoptapet.model.Animal

class FavoriteManager {
    private val _favoriteAnimals = mutableStateMapOf<Int, Animal>()
    val favoriteAnimals: Map<Int, Animal> get() = _favoriteAnimals

    fun toggleFavorite(animal: Animal) {
        if (_favoriteAnimals.containsKey(animal.id)) {
            _favoriteAnimals.remove(animal.id)
        } else {
            _favoriteAnimals[animal.id] = animal
        }
    }

    fun isFavorite(animalId: Int): Boolean {
        return _favoriteAnimals.containsKey(animalId)
    }

    fun getFavorites(): List<Animal> {
        return _favoriteAnimals.values.toList()
    }
}
