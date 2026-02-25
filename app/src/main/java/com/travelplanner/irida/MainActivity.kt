package com.travelplanner.irida

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.travelplanner.irida.ui.screens.ItineraryScreen
import com.travelplanner.irida.ui.screens.PreferencesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Un contenedor básico que ocupa toda la pantalla
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Creamos el controlador que recuerda dónde estamos
                    val navController = rememberNavController()

                    // 2. Definimos el mapa de navegación
                    NavHost(navController = navController, startDestination = "itinerary") {

                        // Pantalla A: Itinerario
                        composable("itinerary") {
                            ItineraryScreen(
                                onNavigateToPreferences = {
                                    // Viajamos a la pantalla de preferencias
                                    navController.navigate("preferences")
                                }
                            )
                        }

                        // Pantalla B: Preferencias
                        composable("preferences") {
                            PreferencesScreen(
                                onNavigateBack = {
                                    // Volveamos a la pantalla anterior
                                    navController.popBackStack()
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}