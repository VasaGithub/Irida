package com.travelplanner.irida.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Preferencias") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Selecciona tu idioma:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* TODO: Inglés */ }, modifier = Modifier.fillMaxWidth()) { Text("English") }
            Button(onClick = { /* TODO: Español */ }, modifier = Modifier.fillMaxWidth()) { Text("Español") }
            Button(onClick = { /* TODO: Catalán */ }, modifier = Modifier.fillMaxWidth()) { Text("Català") }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Text("Volver al itinerario")
            }
        }
    }
}