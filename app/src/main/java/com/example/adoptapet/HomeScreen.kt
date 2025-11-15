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
import coil.compose.AsyncImage
import com.example.adoptapet.model.Animal
import com.example.adoptapet.viewmodel.AnimalViewModel
import com.example.adoptapet.viewmodel.AnimalFilter
import com.example.adoptapet.viewmodel.DisplayLimit

@Composable
fun HomeScreen(
    onAdopterClick: () -> Unit,
    onFavorisClick: () -> Unit,
    onAProposClick: () -> Unit,
    onAnimalClick: (Animal) -> Unit,
    favoritesManager: FavoriteManager
) {
    val animalViewModel = remember { AnimalViewModel() }
    val animals by animalViewModel.animals.collectAsState()
    val isLoading by animalViewModel.isLoading.collectAsState()
    val currentFilter by animalViewModel.currentFilter.collectAsState()
    val displayLimit by animalViewModel.displayLimit.collectAsState()

    var menuExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header avec menu et titre
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bouton menu hamburger
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

                Spacer(modifier = Modifier.width(16.dp))

                // Titre
                Column {
                    Text(
                        text = "Bienvenue dans",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "AdoptaPet !",
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Contenu principal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                if (isLoading) {
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
                                    text = "Découvrez nos ${animals.size} animaux à adopter 🐾",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                // Filtres avec dropdowns
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(bottom = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Filtre Chiens avec dropdown
                                        FilterChipWithLimit(
                                            label = "🐕 Chiens",
                                            filter = AnimalFilter.DOGS,
                                            currentFilter = currentFilter,
                                            displayLimit = displayLimit,
                                            onFilterChange = { animalViewModel.setFilter(it) },
                                            onLimitChange = { animalViewModel.setDisplayLimit(it) }
                                        )

                                        // Filtre Chats avec dropdown
                                        FilterChipWithLimit(
                                            label = "🐈 Chats",
                                            filter = AnimalFilter.CATS,
                                            currentFilter = currentFilter,
                                            displayLimit = displayLimit,
                                            onFilterChange = { animalViewModel.setFilter(it) },
                                            onLimitChange = { animalViewModel.setDisplayLimit(it) }
                                        )

                                        // Filtre Tous avec dropdown
                                        FilterChipWithLimit(
                                            label = "Tous",
                                            filter = AnimalFilter.ALL,
                                            currentFilter = currentFilter,
                                            displayLimit = displayLimit,
                                            onFilterChange = { animalViewModel.setFilter(it) },
                                            onLimitChange = { animalViewModel.setDisplayLimit(it) }
                                        )
                                    }
                                }
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

        // Menu flottant
        if (menuExpanded) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { menuExpanded = false }
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 70.dp)
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
    val isFavorite by remember {
        derivedStateOf {
            favoritesManager.isFavorite(animal.id)
        }
    }

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
            if (animal.imageUrl != null) {
                AsyncImage(
                    model = animal.imageUrl,
                    contentDescription = animal.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(id = animal.imageRes),
                    error = painterResource(id = animal.imageRes)
                )
            } else {
                Image(
                    painter = painterResource(id = animal.imageRes),
                    contentDescription = animal.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

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
                    text = animal.description.take(40) + "...",
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipWithLimit(
    label: String,
    filter: AnimalFilter,
    currentFilter: AnimalFilter,
    displayLimit: DisplayLimit,
    onFilterChange: (AnimalFilter) -> Unit,
    onLimitChange: (DisplayLimit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val isSelected = currentFilter == filter

    val displayText = when {
        isSelected && displayLimit == DisplayLimit.SIX -> "$label (6)"
        isSelected && displayLimit == DisplayLimit.TWELVE -> "$label (12)"
        else -> label
    }

    Box {
        FilterChip(
            selected = isSelected,
            onClick = {
                if (isSelected) {
                    expanded = !expanded
                } else {
                    onFilterChange(filter)
                    onLimitChange(DisplayLimit.ALL)
                }
            },
            label = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(displayText)
                    if (isSelected) {
                        Text("▼", fontSize = 10.sp)
                    }
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Afficher 6") },
                onClick = {
                    onFilterChange(filter)
                    onLimitChange(DisplayLimit.SIX)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Afficher 12") },
                onClick = {
                    onFilterChange(filter)
                    onLimitChange(DisplayLimit.TWELVE)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Afficher tous") },
                onClick = {
                    onFilterChange(filter)
                    onLimitChange(DisplayLimit.ALL)
                    expanded = false
                }
            )
        }
    }
}
