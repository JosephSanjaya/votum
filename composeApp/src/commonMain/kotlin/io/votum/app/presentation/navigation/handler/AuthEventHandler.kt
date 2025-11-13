/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.app.presentation.navigation.handler

import androidx.navigation.NavController
import io.votum.app.presentation.navigation.NavigationEventHandler
import io.votum.auth.presentation.screen.model.LoginScreenIntent
import io.votum.core.presentation.navigation.NavigationIntent
import io.votum.registration.presentation.screen.Registration
import org.koin.core.annotation.Factory

@Factory
class AuthEventHandler : NavigationEventHandler() {

    override fun canHandle(event: NavigationIntent): Boolean {
        return event is LoginScreenIntent.NavigateToSignUp
    }

    override fun navigate(navController: NavController, event: NavigationIntent) {
        when (event) {
            LoginScreenIntent.NavigateToSignUp -> navController.navigate(
                Registration
            )
        }
    }
}
