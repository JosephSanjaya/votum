package io.votum.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.votum.app.presentation.screen.Splash
import io.votum.app.presentation.screen.SplashScreen
import io.votum.auth.presentation.screen.Login
import io.votum.auth.presentation.screen.LoginScreen
import io.votum.core.presentation.component.rememberSnackBarHostState
import io.votum.core.presentation.theme.LocalNavController
import io.votum.core.presentation.theme.LocalSnackBarHost
import io.votum.identity.presentation.screen.IdentityVerification
import io.votum.identity.presentation.screen.IdentityVerificationScreen
import io.votum.onboarding.presentation.screen.Onboarding
import io.votum.onboarding.presentation.screen.OnboardingScreen
import io.votum.registration.presentation.screen.Registration
import io.votum.registration.presentation.screen.RegistrationScreen

@Composable
fun VotumNavHost() {
    val navController = rememberNavController()
    val snackBarHostState = rememberSnackBarHostState()
    NavigationEventBusHandler(navController)

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalSnackBarHost provides snackBarHostState
    ) {
        NavHost(navController = navController, startDestination = Splash) {
            composable<Splash> { SplashScreen() }
            composable<Onboarding> {
                OnboardingScreen()
            }
            composable<Registration> {
                RegistrationScreen()
            }
            composable<IdentityVerification> { backStackEntry ->
                val identityVerification = backStackEntry.toRoute<IdentityVerification>()
                IdentityVerificationScreen(nationalId = identityVerification.nationalId)
            }
            composable<Login> {
                LoginScreen()
            }
        }
    }
}
