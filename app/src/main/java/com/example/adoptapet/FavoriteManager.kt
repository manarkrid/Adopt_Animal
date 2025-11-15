package com.example.adoptapet

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import com.example.adoptapet.database.AnimalDatabase
import com.example.adoptapet.database.FavoriteEntity
import com.example.adoptapet.model.Animal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoriteManager(context: Context) {
    private val database = AnimalDatabase.getDatabase(context)
    private val favoriteDao = database.favoriteDao()
    private val animalDao = database.animalDao()
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private val _favoriteAnimals = mutableStateMapOf<Int, Animal>()
    val favoriteAnimals: Map<Int, Animal> get() = _favoriteAnimals

    init {
        // Charger les favoris depuis la base de données au démarrage
        scope.launch {
            favoriteDao.getAllFavorites().collect { favorites ->
                val animals = animalDao.getAllAnimals().first()
                _favoriteAnimals.clear()
                favorites.forEach { fav ->
                    animals.find { it.id == fav.animalId }?.let { animalEntity ->
                        _favoriteAnimals[fav.animalId] = animalEntity.toAnimal()
                    }
                }
            }
        }
    }

    fun toggleFavorite(animal: Animal) {
        scope.launch {
            if (_favoriteAnimals.containsKey(animal.id)) {
                _favoriteAnimals.remove(animal.id)
                favoriteDao.deleteFavorite(animal.id)
            } else {
                _favoriteAnimals[animal.id] = animal
                favoriteDao.insertFavorite(FavoriteEntity(animal.id))
            }
        }
    }

    fun isFavorite(animalId: Int): Boolean {
        return _favoriteAnimals.containsKey(animalId)
    }

    fun getFavorites(): List<Animal> {
        return _favoriteAnimals.values.toList()
    }
}
