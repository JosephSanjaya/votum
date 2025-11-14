/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.app.presentation.navigation.handler

import androidx.navigation.NavController
import io.votum.app.presentation.navigation.NavigationEventHandler
import io.votum.auth.presentation.screen.Login
import io.votum.core.presentation.navigation.NavigationIntent
import io.votum.identity.presentation.screen.model.IdentityVerificationScreenIntent
import org.koin.core.annotation.Factory

@Factory
class IdentityVerificationEventHandler : NavigationEventHandler() {

    override fun canHandle(event: NavigationIntent): Boolean {
        return event is IdentityVerificationScreenIntent.NavigateToLogin
    }

    override fun navigate(navController: NavController, event: NavigationIntent) {
        when (event) {
            IdentityVerificationScreenIntent.NavigateToLogin -> {
                navController.navigate(Login) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}
