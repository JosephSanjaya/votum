/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.app.presentation.navigation.handler

import androidx.navigation.NavController
import io.votum.app.presentation.navigation.NavigationEventHandler
import io.votum.auth.presentation.screen.Login
import io.votum.core.presentation.navigation.NavigationIntent
import io.votum.identity.presentation.screen.IdentityVerification
import io.votum.registration.presentation.screen.model.RegistrationScreenIntent
import org.koin.core.annotation.Factory

@Factory
class RegistrationEventHandler(
    private val registrationStateProvider: RegistrationStateProvider
) : NavigationEventHandler() {

    override fun canHandle(event: NavigationIntent): Boolean {
        return event is RegistrationScreenIntent.NavigateToLogin ||
            event is RegistrationScreenIntent.NavigateToIdentityVerification
    }

    override fun navigate(navController: NavController, event: NavigationIntent) {
        when (event) {
            RegistrationScreenIntent.NavigateToLogin -> navController.navigate(
                Login
            )
            RegistrationScreenIntent.NavigateToIdentityVerification -> {
                val nationalId = registrationStateProvider.getNationalId()
                navController.navigate(
                    IdentityVerification(nationalId = nationalId)
                )
            }
        }
    }
}
