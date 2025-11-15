package com.example.adoptapet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoptapet.database.AnimalDatabase
import com.example.adoptapet.model.Animal
import com.example.adoptapet.model.AnimalType
import com.example.adoptapet.repository.AnimalRepository
import com.example.adoptapet.service.AnimalApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AnimalFilter {
    ALL, DOGS, CATS
}

enum class DisplayLimit(val value: Int?) {
    SIX(6),
    TWELVE(12),
    ALL(null)
}

class AnimalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AnimalRepository
    private val _allAnimals = MutableStateFlow<List<Animal>>(emptyList())
    
    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentFilter = MutableStateFlow(AnimalFilter.ALL)
    val currentFilter: StateFlow<AnimalFilter> = _currentFilter.asStateFlow()

    private val _displayLimit = MutableStateFlow(DisplayLimit.ALL)
    val displayLimit: StateFlow<DisplayLimit> = _displayLimit.asStateFlow()

    init {
        val database = AnimalDatabase.getDatabase(application)
        val apiService = AnimalApiService.create()
        repository = AnimalRepository(database.animalDao(), apiService)
        
        loadAnimalsFromDatabase()
        refreshAnimalsFromApi()
    }

    private fun loadAnimalsFromDatabase() {
        viewModelScope.launch {
            repository.allAnimals.collect { animals ->
                _allAnimals.value = animals.shuffled()
                applyFilter()
            }
        }
    }

    private fun refreshAnimalsFromApi() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.refreshAnimals()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun setFilter(filter: AnimalFilter) {
        _currentFilter.value = filter
        applyFilter()
    }

    fun setDisplayLimit(limit: DisplayLimit) {
        _displayLimit.value = limit
        applyFilter()
    }

    private fun applyFilter() {
        // Applique d'abord le filtre par type
        val filteredByType = when (_currentFilter.value) {
            AnimalFilter.ALL -> _allAnimals.value
            AnimalFilter.DOGS -> _allAnimals.value.filter { it.type == AnimalType.DOG }
            AnimalFilter.CATS -> _allAnimals.value.filter { it.type == AnimalType.CAT }
        }
        
        // Applique ensuite la limite d'affichage
        _animals.value = when (val limit = _displayLimit.value.value) {
            null -> filteredByType
            else -> filteredByType.take(limit)
        }
    }


}
