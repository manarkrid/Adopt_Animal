package com.example.adoptapet.repository

import com.example.adoptapet.database.AnimalDao
import com.example.adoptapet.database.AnimalEntity
import com.example.adoptapet.model.Animal
import com.example.adoptapet.service.AnimalApiService
import com.example.adoptapet.service.CatBreed
import com.example.adoptapet.service.DogBreed
import com.example.adoptapet.R
import com.example.adoptapet.model.AnimalType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnimalRepository(
    private val animalDao: AnimalDao,
    private val apiService: AnimalApiService
) {
    val allAnimals: Flow<List<Animal>> = animalDao.getAllAnimals().map { entities ->
        entities.map { it.toAnimal() }
    }

    suspend fun refreshAnimals() {
        try {
            // Récupère les données de l'API
            val dogs = apiService.getDogs()
            val cats = apiService.getCats()

            // Convertit en entités
            val dogEntities = dogs
                .filter { it.reference_image_id != null }
                .mapIndexed { index, dog ->
                    AnimalEntity(
                        id = index + 1,
                        name = dog.name ?: "Unknown",
                        description = buildDogDescription(dog),
                        imageRes = R.drawable.dog_vector,
                        imageUrl = "https://cdn2.thedogapi.com/images/${dog.reference_image_id}.jpg",
                        type = AnimalType.DOG.name
                    )
                }

            val catEntities = cats
                .filter { it.reference_image_id != null }
                .mapIndexed { index, cat ->
                    AnimalEntity(
                        id = index + 1001,
                        name = cat.name ?: "Unknown",
                        description = buildCatDescription(cat),
                        imageRes = R.drawable.cat_vector,
                        imageUrl = "https://cdn2.thecatapi.com/images/${cat.reference_image_id}.jpg",
                        type = AnimalType.CAT.name
                    )
                }

            // Sauvegarde dans la base de données
            animalDao.deleteAllAnimals()
            animalDao.insertAnimals(dogEntities + catEntities)
        } catch (e: Exception) {
            e.printStackTrace()
            // Si l'API échoue et qu'il n'y a pas de données en cache, on insère des données de secours
            val count = animalDao.getAnimalsCount()
            if (count == 0) {
                insertFallbackData()
            }
        }
    }

    private suspend fun insertFallbackData() {
        val fallbackAnimals = listOf(
            AnimalEntity(1, "Labrador Retriever", "Chien amical et énergique, parfait pour les familles", R.drawable.dog_vector, null, AnimalType.DOG.name),
            AnimalEntity(2, "Golden Retriever", "Chien doux et intelligent, excellent compagnon", R.drawable.dog_vector, null, AnimalType.DOG.name),
            AnimalEntity(3, "Siamois", "Chat élégant et vocal, très affectueux", R.drawable.cat_vector, null, AnimalType.CAT.name),
            AnimalEntity(4, "Persan", "Chat calme au pelage luxuriant, parfait pour l'intérieur", R.drawable.cat_vector, null, AnimalType.CAT.name),
            AnimalEntity(5, "Berger Allemand", "Chien loyal et intelligent, excellent gardien", R.drawable.dog_vector, null, AnimalType.DOG.name),
            AnimalEntity(6, "Beagle", "Chien curieux et amical, grand renifleur", R.drawable.dog_vector, null, AnimalType.DOG.name),
            AnimalEntity(7, "Main Coon", "Chat géant et doux, au pelage impressionnant", R.drawable.cat_vector, null, AnimalType.CAT.name),
            AnimalEntity(8, "Bengal", "Chat actif au pelage léopard, très joueur", R.drawable.cat_vector, null, AnimalType.CAT.name)
        )
        animalDao.insertAnimals(fallbackAnimals)
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
}
