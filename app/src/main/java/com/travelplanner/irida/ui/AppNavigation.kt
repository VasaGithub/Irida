package com.travelplanner.irida.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.travelplanner.irida.ui.screens.*

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val TRIP_DETAIL = "trip_detail"
    const val GALLERY = "gallery"
    const val PREFERENCES = "preferences"
    const val ABOUT = "about"
    const val TERMS = "terms"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onTripClick = { navController.navigate(Routes.TRIP_DETAIL) },
                onNavigate = { route -> handleBottomNav(route, navController) }
            )
        }

        composable(Routes.TRIP_DETAIL) {
            TripDetailScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route -> handleBottomNav(route, navController) }
            )
        }

        composable(Routes.GALLERY) {
            TripGalleryScreen(
                onNavigate = { route -> handleBottomNav(route, navController) }
            )
        }

        composable(Routes.PREFERENCES) {
            PreferencesScreen(
                onNavigate = { route -> handleBottomNav(route, navController) }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onNavigate = { route -> handleBottomNav(route, navController) }
            )
        }

        composable(Routes.TERMS) {
            TermsAndConditionsScreen(
                onAccept = { navController.popBackStack() },
                onReject = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private fun handleBottomNav(route: String, navController: NavHostController) {
    val destination = when (route) {
        "home" -> Routes.HOME
        "trips" -> Routes.TRIP_DETAIL
        "gallery" -> Routes.GALLERY
        "settings" -> Routes.PREFERENCES
        else -> return
    }
    navController.navigate(destination) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
            inclusive = false
        }
        launchSingleTop = true
        restoreState = true
    }
}