/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.auth.presentation.screen.model

import androidx.compose.material3.SnackbarVisuals
import io.votum.core.presentation.component.DefaultSnackBarVisuals
import io.votum.core.presentation.navigation.NavigationIntent

sealed class LoginScreenIntent {
    data class Login(val email: String, val password: String) : LoginScreenIntent()
    data class EmailChanged(val email: String) : LoginScreenIntent()
    data class PasswordChanged(val password: String) : LoginScreenIntent()
    data object NavigateToSignUp : LoginScreenIntent(), NavigationIntent
    data object NavigateToDashboard : LoginScreenIntent(), NavigationIntent
    data class LoginFailed(override val message: String) :
        LoginScreenIntent(),
        SnackbarVisuals by DefaultSnackBarVisuals(message)
}
