package com.travelplanner.irida.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.travelplanner.irida.ui.screens.*
import com.travelplanner.irida.ui.viewmodels.TripDetailViewModel
import com.travelplanner.irida.ui.viewmodels.TripListViewModel

object Routes {
    const val SPLASH      = "splash"
    const val TERMS       = "terms"
    const val HOME        = "home"

    const val ACTIVITIES  = "activities"
    const val TRIP_DETAIL = "trip_detail"
    const val ADD_TRIP    = "add_trip"
    const val EDIT_TRIP   = "edit_trip"
    const val GALLERY     = "gallery"
    const val PREFERENCES = "preferences"
    const val ABOUT       = "about"

    fun tripDetail(tripId: String) = "$TRIP_DETAIL/$tripId"
    fun editTrip(tripId: String)   = "$EDIT_TRIP/$tripId"

    // ACTUALIZADO: Ahora acepta también un activityId opcional
    fun activities(tripId: String? = null, activityId: String? = null): String {
        var route = ACTIVITIES
        if (tripId != null) {
            route += "?tripId=$tripId"
            if (activityId != null) {
                route += "&activityId=$activityId"
            }
        }
        return route
    }
}

var termsAccepted = false

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {

    val tripListViewModel: TripListViewModel = viewModel()
    val tripDetailViewModel: TripDetailViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    if (termsAccepted) {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
                    } else {
                        navController.navigate(Routes.TERMS) { popUpTo(Routes.SPLASH) { inclusive = true } }
                    }
                }
            )
        }

        composable(Routes.TERMS) {
            TermsAndConditionsScreen(
                onAccept = {
                    termsAccepted = true
                    navController.navigate(Routes.HOME) { popUpTo(Routes.TERMS) { inclusive = true } }
                },
                onReject = { termsAccepted = false },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onTripClick = { trip -> navController.navigate(Routes.tripDetail(trip.id)) },
                onAddTripClick = { navController.navigate(Routes.ADD_TRIP) },
                onEditTripClick = { trip -> navController.navigate(Routes.editTrip(trip.id)) },
                onNavigate = { route -> handleBottomNav(route, navController) },
                viewModel = tripListViewModel
            )
        }

        composable(Routes.ADD_TRIP) {
            AddTripScreen(
                onNavigateBack = { navController.popBackStack() },
                onTripAdded = { navController.popBackStack() },
                viewModel = tripListViewModel
            )
        }

        composable(
            route = "${Routes.EDIT_TRIP}/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            EditTripScreen(
                tripId = tripId,
                onNavigateBack = { navController.popBackStack() },
                onTripUpdated = { navController.popBackStack() },
                viewModel = tripListViewModel
            )
        }

        composable(
            route = "${Routes.TRIP_DETAIL}/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            TripDetailScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> handleBottomNav(route, navController) },
                onAddActivity = { navController.navigate(Routes.activities(tripId)) },
                // NUEVO: Conectamos la edición con la ruta pasando ambos IDs
                onEditActivity = { activityId -> navController.navigate(Routes.activities(tripId, activityId)) },
                viewModel = tripDetailViewModel
            )
        }

        // ACTUALIZADO: Añadimos activityId a los argumentos que espera la ruta
        composable(
            route = "${Routes.ACTIVITIES}?tripId={tripId}&activityId={activityId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("activityId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")
            val activityId = backStackEntry.arguments?.getString("activityId")
            AddActivityScreen(
                initialTripId = tripId,
                initialActivityId = activityId, // Se lo pasamos a la pantalla
                onNavigate = { route -> handleBottomNav(route, navController) },
                onReturn = { navController.popBackStack() },
                tripListViewModel = tripListViewModel,
                tripDetailViewModel = tripDetailViewModel
            )
        }

        composable(Routes.GALLERY) {
            TripGalleryScreen(onNavigate = { route -> handleBottomNav(route, navController) })
        }

        composable(Routes.PREFERENCES) {
            PreferencesScreen(onNavigate = { route -> handleBottomNav(route, navController) })
        }

        composable(Routes.ABOUT) {
            AboutScreen(onNavigate = { route -> handleBottomNav(route, navController) })
        }
    }
}

private fun handleBottomNav(route: String, navController: NavHostController) {
    val destination = when (route) {
        "home"          -> Routes.HOME
        "activities"    -> Routes.ACTIVITIES
        "gallery"       -> Routes.GALLERY
        "settings"      -> Routes.PREFERENCES
        "about"         -> Routes.ABOUT
        "terms"         -> Routes.TERMS
        else            -> return
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