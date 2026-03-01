package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelplanner.irida.ui.theme.*

data class GalleryImage(
    val id: String,
    val emoji: String,
    val label: String,
    val isTop: Boolean = false
)

val mockGalleryImages = listOf(
    GalleryImage("1", "🗼", "Tokyo Tower", isTop = true),
    GalleryImage("2", "⛩️", "Senso-ji", isTop = false),
    GalleryImage("3", "🌸", "Ueno Park", isTop = true),
    GalleryImage("4", "🍜", "Ramen Ippudo", isTop = false),
    GalleryImage("5", "🏯", "Osaka Castle", isTop = false),
    GalleryImage("6", "🌃", "Shibuya Night", isTop = true),
    GalleryImage("7", "🍣", "Sushi Saito", isTop = false),
    GalleryImage("8", "🚄", "Shinkansen", isTop = false),
    GalleryImage("9", "🌿", "Arashiyama", isTop = false)
)

@Composable
fun TripGalleryScreen(
    onNavigate: (String) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Todas") }
    var images by remember { mutableStateOf(mockGalleryImages) }
    var imageToDelete by remember { mutableStateOf<GalleryImage?>(null) }
    val filters = listOf("Todas", "★ Top", "Vídeos")

    val filteredImages = when (selectedFilter) {
        "★ Top" -> images.filter { it.isTop }
        else -> images
    }

    imageToDelete?.let { image ->
        AlertDialog(
            onDismissRequest = { imageToDelete = null },
            containerColor = NavyLight,
            title = {
                Text("Eliminar foto", color = White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "¿Seguro que quieres eliminar ${image.label}?",
                    color = GrayMid
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        images = images.filter { it.id != image.id }
                        imageToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Eliminar", color = White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { imageToDelete = null },
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayDark)
                ) {
                    Text("Cancelar", color = GrayMid)
                }
            }
        )
    }

    Scaffold(
        containerColor = NavyDeep,
        bottomBar = {
            BottomNavBar(selectedTab = 2, onTabSelected = { tab ->
                when (tab) {
                    0 -> onNavigate("home")
                    1 -> onNavigate("trips")
                    2 -> onNavigate("gallery")
                    3 -> onNavigate("settings")
                }
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep)
                .padding(paddingValues)
        ) {
            // Header
            GalleryHeader(imageCount = images.size)

            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selectedFilter == filter) NavyDeep else GrayMid
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TurquoisePrimary,
                            containerColor = NavyLight
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == filter,
                            borderColor = GrayDark,
                            selectedBorderColor = TurquoisePrimary
                        )
                    )
                }
            }

            // Count and sort row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredImages.size} elementos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMid
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ordenar: Fecha ▾",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TurquoisePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Image grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Add button cell
                item {
                    AddImageCell(onClick = { /* @TODO implement image picker */ })
                }

                // Image cells
                items(filteredImages) { image ->
                    ImageCell(
                        image = image,
                        onDelete = { imageToDelete = image }
                    )
                }
            }
        }
    }
}

@Composable
fun GalleryHeader(imageCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Tokyo Adventure",
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "$imageCount fotos · 234 MB",
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMid
            )
        }
        Button(
            onClick = { /* @TODO implement add photo */ },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TurquoisePrimary,
                contentColor = NavyDeep
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Añadir", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AddImageCell(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(NavyLight)
            .border(1.dp, TurquoisePrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir foto",
                tint = TurquoisePrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "AÑADIR",
                style = MaterialTheme.typography.labelSmall,
                color = TurquoisePrimary,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ImageCell(image: GalleryImage, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(NavyLight, NavyMid)
                )
            )
    ) {
        // Emoji as image placeholder
        Text(
            text = image.emoji,
            fontSize = 40.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        // Top badge
        if (image.isTop) {
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.TopStart)
                    .background(GoldAccent, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = NavyDeep,
                        modifier = Modifier.size(8.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "TOP",
                        style = MaterialTheme.typography.labelSmall,
                        color = NavyDeep,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Delete button
        Box(
            modifier = Modifier
                .padding(6.dp)
                .size(20.dp)
                .align(Alignment.TopEnd)
                .background(ErrorRed.copy(alpha = 0.9f), CircleShape)
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar",
                tint = White,
                modifier = Modifier.size(12.dp)
            )
        }

        // Label at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(NavyDeep.copy(alpha = 0.7f))
                .padding(4.dp)
        ) {
            Text(
                text = image.label,
                style = MaterialTheme.typography.labelSmall,
                color = WhiteSoft,
                fontSize = 9.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripGalleryScreenPreview() {
    IridaTheme {
        TripGalleryScreen()
    }
}