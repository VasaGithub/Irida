package com.travelplanner.irida.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.travelplanner.irida.ui.screens.AboutScreen
import com.travelplanner.irida.ui.screens.AccessLogScreen
import com.travelplanner.irida.ui.screens.AddActivityScreen
import com.travelplanner.irida.ui.screens.AddTripScreen
import com.travelplanner.irida.ui.screens.EditTripScreen
import com.travelplanner.irida.ui.screens.ForgotPasswordScreen
import com.travelplanner.irida.ui.screens.HomeScreen
import com.travelplanner.irida.ui.screens.LoginScreen
import com.travelplanner.irida.ui.screens.PreferencesScreen
import com.travelplanner.irida.ui.screens.RegisterScreen
import com.travelplanner.irida.ui.screens.SplashScreen
import com.travelplanner.irida.ui.screens.TermsAndConditionsScreen
import com.travelplanner.irida.ui.screens.TripDetailScreen
import com.travelplanner.irida.ui.screens.TripGalleryScreen
import com.travelplanner.irida.ui.screens.UserProfileScreen
import com.travelplanner.irida.ui.viewmodels.AuthViewModel
import com.travelplanner.irida.ui.viewmodels.TripDetailViewModel
import com.travelplanner.irida.ui.viewmodels.TripListViewModel

object Routes {
    const val SPLASH          = "splash"
    const val TERMS           = "terms"
    const val HOME            = "home"
    const val LOGIN           = "login"
    const val REGISTER        = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val ACTIVITIES      = "activities"
    const val TRIP_DETAIL     = "trip_detail"
    const val ADD_TRIP        = "add_trip"
    const val EDIT_TRIP       = "edit_trip"
    const val GALLERY         = "gallery"
    const val PREFERENCES     = "preferences"
    const val ABOUT           = "about"
    const val PROFILE         = "profile"
    const val ACCESS_LOG      = "access_log"

    fun tripDetail(tripId: String) = "$TRIP_DETAIL/$tripId"
    fun editTrip(tripId: String)   = "$EDIT_TRIP/$tripId"

    fun activities(tripId: String? = null, activityId: String? = null): String {
        var route = ACTIVITIES
        if (tripId != null) {
            route += "?tripId=$tripId"
            if (activityId != null) route += "&activityId=$activityId"
        }
        return route
    }
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {

    val tripListViewModel: TripListViewModel = hiltViewModel()
    val tripDetailViewModel: TripDetailViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    if (authViewModel.isLoggedIn) {
                        navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
                    } else {
                        navController.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) { popUpTo(Routes.REGISTER) { inclusive = true } }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.TERMS) {
            TermsAndConditionsScreen(
                onAccept = { navController.navigate(Routes.HOME) { popUpTo(Routes.TERMS) { inclusive = true } } },
                onReject = {},
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
                onEditActivity = { activityId -> navController.navigate(Routes.activities(tripId, activityId)) },
                viewModel = tripDetailViewModel
            )
        }

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
                initialActivityId = activityId,
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
            PreferencesScreen(
                onNavigate = { route -> handleBottomNav(route, navController) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToAccessLog = { navController.navigate(Routes.ACCESS_LOG) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.PROFILE) {
            UserProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ACCESS_LOG) {
            AccessLogScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(onNavigate = { route -> handleBottomNav(route, navController) })
        }
    }
}

private fun handleBottomNav(route: String, navController: NavHostController) {
    val destination = when (route) {
        "home"       -> Routes.HOME
        "activities" -> Routes.ACTIVITIES
        "gallery"    -> Routes.GALLERY
        "settings"   -> Routes.PREFERENCES
        "about"      -> Routes.ABOUT
        "terms"      -> Routes.TERMS
        else         -> return
    }
    navController.navigate(destination) {
        popUpTo(navController.graph.startDestinationId) { saveState = true; inclusive = false }
        launchSingleTop = true
        restoreState = true
    }
}
