package com.example.adoptapet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AProposScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    fun openInBrowser(url: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        // Force l'ouverture dans le navigateur
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome") // Chrome
        try {
            context.startActivity(intent)
        } catch (e: android.content.ActivityNotFoundException) {
            // Si Chrome n'est pas installé, essayer avec le navigateur par défaut
            intent.setPackage(null)
            context.startActivity(intent)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onBack) {
                Text("Retour")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "À propos",
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo/Icône de l'app
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🐾",
                    fontSize = 64.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "AdoptaPet",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = buildAnnotatedString {
                    append("Documentaire Animalier sur ")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF8B2BE2),
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("YouTube")
                    }
                },
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable {
                        openInBrowser("https://www.youtube.com/watch?v=k2o6XjDsvpA&feature=emb_title")
                    }
            )


            Spacer(modifier = Modifier.height(32.dp))

            // Description
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Notre Mission",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "AdoptaPet est une application dédiée à faciliter l'adoption d'animaux de compagnie. " +
                                "Nous connectons les animaux à la recherche d'un foyer avec des familles aimantes.",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fonctionnalités
            InfoCard(
                icon = Icons.Default.FavoriteBorder,
                title = "Découvrez",
                description = "Parcourez des centaines de chiens et chats à adopter"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(
                icon = Icons.Default.Favorite,
                title = "Favoris",
                description = "Sauvegardez vos animaux préférés pour les retrouver facilement"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(
                icon = Icons.Default.Info,
                title = "Informations détaillées",
                description = "Consultez les caractéristiques et tempéraments de chaque animal"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Text(
                text = "Données fournies par",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "TheDogAPI & TheCatAPI",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "© 2024 AdoptaPet",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InfoCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
