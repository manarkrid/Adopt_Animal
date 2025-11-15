package com.example.adoptapet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoptapet.R
import com.example.adoptapet.model.Animal
import com.example.adoptapet.model.AnimalType
import com.example.adoptapet.service.AnimalApiService
import com.example.adoptapet.service.DogBreed
import com.example.adoptapet.service.CatBreed
import kotlinx.coroutines.async
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

class AnimalViewModel : ViewModel() {
    private val _allAnimals = MutableStateFlow<List<Animal>>(emptyList())
    
    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentFilter = MutableStateFlow(AnimalFilter.ALL)
    val currentFilter: StateFlow<AnimalFilter> = _currentFilter.asStateFlow()

    private val _displayLimit = MutableStateFlow(DisplayLimit.ALL)
    val displayLimit: StateFlow<DisplayLimit> = _displayLimit.asStateFlow()

    private val apiService = AnimalApiService.create()

    init {
        loadAnimalsFromApi()
    }

    fun loadAnimalsFromApi() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Récupère les chiens et chats en parallèle
                val dogsDeferred = async { apiService.getDogs() }
                val catsDeferred = async { apiService.getCats() }

                val dogs = dogsDeferred.await()
                val cats = catsDeferred.await()

                // Convertit les chiens en animaux
                val dogAnimals = dogs.mapIndexed { index, dog ->
                    Animal(
                        id = index + 1,
                        name = dog.name ?: "Unknown",
                        description = buildDogDescription(dog),
                        imageRes = getDogImageResource(dog.reference_image_id),
                        imageUrl = if (dog.reference_image_id != null)
                            "https://cdn2.thedogapi.com/images/${dog.reference_image_id}.jpg"
                        else null,
                        type = AnimalType.DOG
                    )
                }

                // Convertit les chats en animaux
                val catAnimals = cats.mapIndexed { index, cat ->
                    Animal(
                        id = index + 1001, // IDs différents pour éviter les conflits
                        name = cat.name ?: "Unknown",
                        description = buildCatDescription(cat),
                        imageRes = getCatImageResource(cat.reference_image_id),
                        imageUrl = if (cat.reference_image_id != null)
                            "https://cdn2.thecatapi.com/images/${cat.reference_image_id}.jpg"
                        else null,
                        type = AnimalType.CAT
                    )
                }

                // Combine et mélange les animaux
                val allAnimals = (dogAnimals + catAnimals).toList().shuffled()
                _allAnimals.value = allAnimals
                applyFilter()

            } catch (e: Exception) {
                e.printStackTrace()
                // En cas d'erreur, utiliser des données de secours
                _allAnimals.value = getFallbackAnimals()
                applyFilter()
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

    private fun buildDogDescription(dog: DogBreed): String {
        val description = StringBuilder()
        description.append("Race de chien")

        dog.bred_for?.let {
            description.append(" élevé pour : $it")
        }

        dog.temperament?.let {
            description.append(". Tempérament : $it")
        }

        description.append(". Espérance de vie : ${dog.life_span}")

        return description.toString()
    }

    private fun buildCatDescription(cat: CatBreed): String {
        return "Race de chat. ${cat.description}. " +
                "Tempérament : ${cat.temperament}. " +
                "Origine : ${cat.origin}. " +
                "Espérance de vie : ${cat.life_span}"
    }

    private fun getDogImageResource(imageId: String?): Int {
        // Utilise des images locales basées sur le type de chien
        return when {
            imageId?.contains("hound") == true -> R.drawable.dog_vector
            imageId?.contains("terrier") == true -> R.drawable.dog_vector
            else -> R.drawable.dog_vector
        }
    }

    private fun getCatImageResource(imageId: String?): Int {
        // Utilise des images locales pour les chats
        return R.drawable.cat_vector
    }

    private fun getFallbackAnimals(): List<Animal> {
        // Données de secours si l'API échoue
        return listOf(
            Animal(1, "Labrador Retriever", "Chien amical et énergique, parfait pour les familles", R.drawable.dog_vector, type = AnimalType.DOG),
            Animal(2, "Golden Retriever", "Chien doux et intelligent, excellent compagnon", R.drawable.dog_vector, type = AnimalType.DOG),
            Animal(3, "Siamois", "Chat élégant et vocal, très affectueux", R.drawable.cat_vector, type = AnimalType.CAT),
            Animal(4, "Persan", "Chat calme au pelage luxuriant, parfait pour l'intérieur", R.drawable.cat_vector, type = AnimalType.CAT),
            Animal(5, "Berger Allemand", "Chien loyal et intelligent, excellent gardien", R.drawable.dog_vector, type = AnimalType.DOG),
            Animal(6, "Beagle", "Chien curieux et amical, grand renifleur", R.drawable.dog_vector, type = AnimalType.DOG),
            Animal(7, "Main Coon", "Chat géant et doux, au pelage impressionnant", R.drawable.cat_vector, type = AnimalType.CAT),
            Animal(8, "Bengal", "Chat actif au pelage léopard, très joueur", R.drawable.cat_vector, type = AnimalType.CAT)
        )
    }
}
