package com.travelplanner.irida.ui

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.travelplanner.irida.ui.screens.*

object Routes {
    const val SPLASH = "splash"
    const val TERMS = "terms"
    const val HOME = "home"
    const val TRIP_DETAIL = "trip_detail"
    const val GALLERY = "gallery"
    const val PREFERENCES = "preferences"
    const val ABOUT = "about"
}

var termsAccepted = false

@Preview
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    if (termsAccepted) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.TERMS) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.TERMS) {
            TermsAndConditionsScreen(
                onAccept = {
                    termsAccepted = true
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.TERMS) { inclusive = true }
                    }
                },
                onReject = {
                    termsAccepted = false
                },
                onBack = { navController.popBackStack() }
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
    }
}

private fun handleBottomNav(route: String, navController: NavHostController) {
    val destination = when (route) {
        "home" -> Routes.HOME
        "trips" -> Routes.TRIP_DETAIL
        "gallery" -> Routes.GALLERY
        "settings" -> Routes.PREFERENCES
        "about" -> Routes.ABOUT
        "terms" -> Routes.TERMS
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