/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.app.presentation.navigation.handler

import androidx.navigation.NavController
import io.votum.app.presentation.navigation.NavigationEventHandler
import io.votum.core.presentation.navigation.NavigationIntent
import io.votum.onboarding.presentation.screen.model.OnboardingScreenIntent
import io.votum.registration.presentation.screen.Registration
import org.koin.core.annotation.Factory

@Factory
class OnboardingEventHandler : NavigationEventHandler() {

    override fun canHandle(event: NavigationIntent): Boolean {
        return event is OnboardingScreenIntent.NavigateToSignUp
    }

    override fun navigate(navController: NavController, event: NavigationIntent) {
        when (event) {
            OnboardingScreenIntent.NavigateToSignUp -> navController.navigate(
                Registration
            )
        }
    }
}
