/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.app.presentation.navigation.handler

import androidx.navigation.NavController
import io.votum.app.presentation.navigation.NavigationEventHandler
import io.votum.app.presentation.screen.Splash
import io.votum.app.presentation.screen.SplashScreenUiEvent
import io.votum.core.presentation.navigation.NavigationEvent
import io.votum.onboarding.presentation.screen.Onboarding
import org.koin.core.annotation.Factory

@Factory
class SplashEventHandler : NavigationEventHandler() {

    override fun canHandle(event: NavigationEvent): Boolean {
        return event is SplashScreenUiEvent.NavigateToOnBoarding
    }

    override fun navigate(navController: NavController, event: NavigationEvent) {
        when (event) {
            SplashScreenUiEvent.NavigateToOnBoarding -> navController.navigate(
                Onboarding
            ) {
                popUpTo(Splash) {
                    inclusive = true
                }
            }
        }
    }
}
