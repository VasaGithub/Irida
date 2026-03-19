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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travelplanner.irida.R
import com.travelplanner.irida.ui.theme.*
import com.travelplanner.irida.data.mocks.mockGalleryImages
import com.travelplanner.irida.domain.GalleryImage

@Composable
fun TripGalleryScreen(
    onNavigate: (String) -> Unit = {}
) {
    // Usamos los strings traducidos para el estado inicial y las opciones de filtro
    val filterAll = stringResource(R.string.filter_todas)
    val filterTop = stringResource(R.string.filter_top)
    val filterVideos = stringResource(R.string.filter_videos)

    var selectedFilter by remember { mutableStateOf(filterAll) }
    var images by remember { mutableStateOf(mockGalleryImages) }
    var imageToDelete by remember { mutableStateOf<GalleryImage?>(null) }
    val filters = listOf(filterAll, filterTop, filterVideos)

    val filteredImages = when (selectedFilter) {
        filterTop -> images.filter { it.isTop }
        else -> images
    }

    imageToDelete?.let { image ->
        AlertDialog(
            onDismissRequest = { imageToDelete = null },
            containerColor = NavyLight,
            title = {
                Text(stringResource(R.string.dialog_title_eliminar_foto), color = White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    stringResource(R.string.dialog_desc_eliminar_foto, image.label),
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
                    Text(stringResource(R.string.btn_eliminar), color = White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { imageToDelete = null },
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayDark)
                ) {
                    Text(stringResource(R.string.btn_cancelar), color = GrayMid)
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
                    1 -> onNavigate("activities")
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
                    text = stringResource(R.string.label_elementos_count, filteredImages.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrayMid
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.sort_by_date),
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
                // Esto probablemente vendrá del ViewModel en el futuro, por ahora lo dejamos traducido
                text = stringResource(R.string.mock_title_aventura_tokio),
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stringResource(R.string.label_fotos_mb, imageCount, 234), // 234 MB hardcoded por ahora
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
            Text(stringResource(R.string.btn_anadir), fontWeight = FontWeight.Bold)
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
                contentDescription = stringResource(R.string.cd_anadir_foto),
                tint = TurquoisePrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.label_anadir_caps),
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
                        text = stringResource(R.string.badge_top),
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
                contentDescription = stringResource(R.string.cd_eliminar),
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