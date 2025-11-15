package com.example.adoptapet
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.example.adoptapet.model.Animal

sealed class Screen {
    object Home : Screen()
    object Adopter : Screen()
    object Favoris : Screen()
    object APropos : Screen()
    data class AnimalDetail(val animal: Animal) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdoptaPetApp()
        }
    }
}

@Composable
fun AdoptaPetApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val favoritesManager = remember { FavoriteManager() }
    val adoptionManager = remember { AdoptionManager() }

    MaterialTheme {
        when (currentScreen) {
            is Screen.Home -> HomeScreen(
                onAdopterClick = { currentScreen = Screen.Adopter },
                onFavorisClick = { currentScreen = Screen.Favoris },
                onAProposClick = { currentScreen = Screen.APropos },
                onAnimalClick = { animal ->
                    currentScreen = Screen.AnimalDetail(animal)
                },
                favoritesManager = favoritesManager
            )
            is Screen.Adopter -> AdopterScreen(
                onBack = { currentScreen = Screen.Home },
                adoptionManager = adoptionManager,
                onAnimalClick = { animal ->
                    currentScreen = Screen.AnimalDetail(animal)
                }
            )
            is Screen.Favoris -> Text("Favoris - À implémenter")
            is Screen.APropos -> Text("À propos - À implémenter")
            is Screen.AnimalDetail -> {
                val animal = (currentScreen as Screen.AnimalDetail).animal
                AnimalDetailScreen(
                    animal = animal,
                    onBackClick = { currentScreen = Screen.Home },
                    favoritesManager = favoritesManager,
                    adoptionManager = adoptionManager
                )
            }
        }
    }
}