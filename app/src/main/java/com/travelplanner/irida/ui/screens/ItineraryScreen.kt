package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val actividadesDePrueba = listOf(
    "Vuelo: Barcelona - Tokio",
    "Check-in: Hotel Shinjuku",
    "Cena: Restaurante Ichiran Ramen",
    "Visita: Templo Senso-ji"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(onNavigateToPreferences: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Itinerario") },
                actions = {
                    IconButton(onClick = onNavigateToPreferences) { Text("⚙️") }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(actividadesDePrueba) { actividad ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(actividad, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}