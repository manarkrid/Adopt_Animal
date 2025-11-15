package com.example.adoptapet

import androidx.compose.runtime.mutableStateMapOf
import com.example.adoptapet.model.Animal

class AdoptionManager {
    private val _adoptedAnimals = mutableStateMapOf<Int, Animal>()
    val adoptedAnimals: Map<Int, Animal> get() = _adoptedAnimals

    fun adoptAnimal(animal: Animal) {
        if (!_adoptedAnimals.containsKey(animal.id)) {
            _adoptedAnimals[animal.id] = animal
        }
    }

    fun isAdopted(animalId: Int): Boolean {
        return _adoptedAnimals.containsKey(animalId)
    }

    fun getAdoptedAnimals(): List<Animal> {
        return _adoptedAnimals.values.toList()
    }

    fun removeAdoption(animalId: Int) {
        _adoptedAnimals.remove(animalId)
    }
}
