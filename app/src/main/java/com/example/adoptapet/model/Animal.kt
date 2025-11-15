package com.example.adoptapet.model

enum class AnimalType {
    DOG, CAT
}

data class Animal(
    val id: Int,
    val name: String,
    val description: String,
    val imageRes: Int,
    val imageUrl: String? = null,
    val type: AnimalType
)
