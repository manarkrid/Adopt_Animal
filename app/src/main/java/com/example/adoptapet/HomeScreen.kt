package com.example.adoptapet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

data class Animal(val name: String, val description: String, val imageRes: Int)

@Composable
fun HomeScreen() {
    val animals = listOf(
        Animal("Chat mignon", "Un chat adorable à adopter", R.drawable.cat_image),
        Animal("Chien joueur", "Un chien joueur et affectueux", R.drawable.dog_image),
        Animal("Lapin doux", "Un petit lapin très câlin", R.drawable.rabbit_image),
        Animal("Cheval brown", "Un cheval brown adorable", R.drawable.cheval_image)
    )

    var menuExpanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Colonne pour le bouton hamburger
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

        // Menu flottant avec Popup
        if (menuExpanded) {
            Popup(alignment = Alignment.TopStart) {
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                ) {
                    FloatingMenuItem("Accueil") { menuExpanded = false }
                    FloatingMenuItem("Chats") { menuExpanded = false }
                    FloatingMenuItem("Chiens") { menuExpanded = false }
                    FloatingMenuItem("À propos") { menuExpanded = false }
                }
            }
        }

        // Contenu principal centré et scrollable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally, // centre les items
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        text = "Bienvenue dans AdoptaPet !",
                        fontSize = 28.sp,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                             // centre le texte du header
                    )
                }
                items(animals) { animal ->
                    AnimalCard(animal)
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
            .padding(vertical = 8.dp, horizontal = 12.dp)
    )
}

@Composable
fun AnimalCard(animal: Animal) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth(0.85f) // Utilise 85% de la largeur disponible pour un bon centrage
            .height(220.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray) // fond uniforme pour toutes les cartes
        ) {
            Image(
                painter = painterResource(id = animal.imageRes),
                contentDescription = animal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomStart)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = animal.name,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = animal.description,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// Fonction principale pour l'application
@Composable
fun AdoptaPetApp() {
    MaterialTheme {
        HomeScreen()
    }
}