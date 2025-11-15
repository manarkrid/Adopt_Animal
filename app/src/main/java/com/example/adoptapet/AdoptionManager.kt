package com.example.adoptapet

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import com.example.adoptapet.database.AnimalDatabase
import com.example.adoptapet.database.AdoptionEntity
import com.example.adoptapet.model.Animal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdoptionManager(context: Context) {
    private val database = AnimalDatabase.getDatabase(context)
    private val adoptionDao = database.adoptionDao()
    private val animalDao = database.animalDao()
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private val _adoptedAnimals = mutableStateMapOf<Int, Animal>()
    val adoptedAnimals: Map<Int, Animal> get() = _adoptedAnimals

    init {
        // Charger les adoptions depuis la base de données au démarrage
        scope.launch {
            adoptionDao.getAllAdoptions().collect { adoptions ->
                val animals = animalDao.getAllAnimals().first()
                _adoptedAnimals.clear()
                adoptions.forEach { adoption ->
                    animals.find { it.id == adoption.animalId }?.let { animalEntity ->
                        _adoptedAnimals[adoption.animalId] = animalEntity.toAnimal()
                    }
                }
            }
        }
    }

    fun adoptAnimal(animal: Animal) {
        scope.launch {
            if (!_adoptedAnimals.containsKey(animal.id)) {
                _adoptedAnimals[animal.id] = animal
                adoptionDao.insertAdoption(AdoptionEntity(animal.id))
            }
        }
    }

    fun isAdopted(animalId: Int): Boolean {
        return _adoptedAnimals.containsKey(animalId)
    }

    fun getAdoptedAnimals(): List<Animal> {
        return _adoptedAnimals.values.toList()
    }

    fun removeAdoption(animalId: Int) {
        scope.launch {
            _adoptedAnimals.remove(animalId)
            adoptionDao.deleteAdoption(animalId)
        }
    }
}
