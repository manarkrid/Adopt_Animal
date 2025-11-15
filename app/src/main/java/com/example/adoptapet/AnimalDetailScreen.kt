package com.example.adoptapet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.adoptapet.model.Animal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(
    animal: Animal,
    onBackClick: () -> Unit,
    favoritesManager: FavoriteManager,
    adoptionManager: AdoptionManager
) {
    val isFavorite by remember { derivedStateOf {
        favoritesManager.isFavorite(animal.id)
    }}
    
    val isAdopted by remember { derivedStateOf {
        adoptionManager.isAdopted(animal.id)
    }}
    
    var showAdoptionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(animal.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    // Bouton favori dans la barre d'actions
                    IconButton(
                        onClick = { favoritesManager.toggleFavorite(animal) }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Image de l'animal
            if (animal.imageUrl != null) {
                AsyncImage(
                    model = animal.imageUrl,
                    contentDescription = animal.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    placeholder = painterResource(id = animal.imageRes),
                    error = painterResource(id = animal.imageRes)
                )
            } else {
                Image(
                    painter = painterResource(id = animal.imageRes),
                    contentDescription = animal.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }

            // Contenu détaillé
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = animal.name,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = animal.description,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Informations supplémentaires
                Text(
                    text = "Caractéristiques",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Affectueux et joueur\n• Vacciné et stérilisé\n• Convient aux familles",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Bouton d'adoption
                Button(
                    onClick = { 
                        if (!isAdopted) {
                            showAdoptionDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAdopted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAdopted) Color.Gray else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (isAdopted) "Déjà adopté ✓" else "Adopter cet animal",
                        fontSize = 18.sp
                    )
                }
            }
        }
        
        // Dialog de confirmation d'adoption
        if (showAdoptionDialog) {
            AlertDialog(
                onDismissRequest = { showAdoptionDialog = false },
                title = { Text("Confirmer l'adoption") },
                text = { Text("Voulez-vous vraiment adopter ${animal.name} ? 🐾") },
                confirmButton = {
                    Button(
                        onClick = {
                            adoptionManager.adoptAnimal(animal)
                            showAdoptionDialog = false
                        }
                    ) {
                        Text("Oui, adopter !")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAdoptionDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}