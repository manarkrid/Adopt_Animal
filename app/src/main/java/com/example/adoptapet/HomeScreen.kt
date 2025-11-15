package com.example.adoptapet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adoptapet.model.Animal
import com.example.adoptapet.viewmodel.AnimalViewModel

@Composable
fun HomeScreen(
    onAdopterClick: () -> Unit,
    onFavorisClick: () -> Unit,
    onAProposClick: () -> Unit,
    onAnimalClick: (Animal) -> Unit,
    favoritesManager: FavoriteManager
) {
    val animalViewModel: AnimalViewModel = viewModel()
    val animals by animalViewModel.animals.collectAsState()
    val isLoading by animalViewModel.isLoading.collectAsState()

    var menuExpanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Menu hamburger
        Column(
            modifier = Modifier
                .width(60.dp)
                .fillMaxHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { menuExpanded = !menuExpanded },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Menu flottant
        if (menuExpanded) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { menuExpanded = false }
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                ) {
                    FloatingMenuItem("Adopter") { menuExpanded = false; onAdopterClick() }
                    FloatingMenuItem("Favoris") { menuExpanded = false; onFavorisClick() }
                    FloatingMenuItem("À propos") { menuExpanded = false; onAProposClick() }
                }
            }
        }

        // Contenu principal avec grille 2 colonnes
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                // Indicateur de chargement
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chargement des animaux...")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { GridItemSpan(2) }) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Bienvenue dans AdoptaPet !",
                                fontSize = 28.sp,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Découvrez nos ${animals.size} animaux à adopter 🐾",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )
                        }
                    }

                    items(animals) { animal ->
                        AnimalCardMini(
                            animal = animal,
                            onClick = { onAnimalClick(animal) },
                            favoritesManager = favoritesManager
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingMenuItem(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        fontSize = 16.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp)
    )
}

@Composable
fun AnimalCardMini(
    animal: Animal,
    onClick: () -> Unit,
    favoritesManager: FavoriteManager
) {
    val isFavorite by remember { derivedStateOf {
        favoritesManager.isFavorite(animal.id)
    }}

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = animal.imageRes),
                contentDescription = animal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Bouton favori
            IconButton(
                onClick = { favoritesManager.toggleFavorite(animal) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(
                        Color.White.copy(alpha = 0.8f),
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }

            // Overlay texte en bas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = animal.name,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = animal.description.take(25) + "...",
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1
                )
            }
        }
    }
}