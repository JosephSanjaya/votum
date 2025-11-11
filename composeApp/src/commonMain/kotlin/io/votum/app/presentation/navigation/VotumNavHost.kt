package io.votum.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.votum.app.presentation.screen.Splash
import io.votum.app.presentation.screen.SplashScreen
import io.votum.core.presentation.component.rememberSnackBarHostState
import io.votum.onboarding.presentation.screen.Onboarding
import io.votum.onboarding.presentation.screen.OnboardingScreen

@Composable
fun VotumNavHost() {
    val navController = rememberNavController()
    val snackbarHostState = rememberSnackBarHostState()
    NavigationEventBusHandler(navController)
    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> { SplashScreen() }
        composable<Onboarding> {
            OnboardingScreen(
                snackbarHostState = snackbarHostState
            )
        }
    }
}
