package com.example.adoptapet.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface DogApiService {
    @GET("v1/breeds?limit=50")
    suspend fun getDogs(): List<DogBreed>
}

interface CatApiService {
    @GET("v1/breeds")
    suspend fun getCats(): List<CatBreed>
}

class AnimalApiService {
    companion object {
        private const val DOG_BASE_URL = "https://api.thedogapi.com/"
        private const val CAT_BASE_URL = "https://api.thecatapi.com/"

        fun create(): AnimalApiService {
            return AnimalApiService()
        }
    }

    private val dogApi: DogApiService = Retrofit.Builder()
        .baseUrl(DOG_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DogApiService::class.java)

    private val catApi: CatApiService = Retrofit.Builder()
        .baseUrl(CAT_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CatApiService::class.java)

    suspend fun getDogs(): List<DogBreed> = dogApi.getDogs()
    suspend fun getCats(): List<CatBreed> = catApi.getCats()
}

// Modèles pour l'API des chiens
data class DogBreed(
        val id: Int,
        val name: String,
        val bred_for: String?,
        val breed_group: String?,
        val life_span: String,
        val temperament: String?,
        val origin: String?,
        val reference_image_id: String?
)

// Modèles pour l'API des chats
data class CatBreed(
        val id: String,
        val name: String,
        val description: String,
        val temperament: String,
        val origin: String,
        val life_span: String,
        val reference_image_id: String?
)
