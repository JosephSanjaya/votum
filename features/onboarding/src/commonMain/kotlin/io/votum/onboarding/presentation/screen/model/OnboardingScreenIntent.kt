package io.votum.onboarding.presentation.screen.model

import androidx.compose.material3.SnackbarVisuals
import io.votum.core.presentation.component.DefaultSnackBarVisuals
import io.votum.core.presentation.navigation.NavigationIntent

sealed interface OnboardingScreenIntent {
    data object ToggleSignInSheet :
        OnboardingScreenIntent

    data object OnSignInClicked :
        OnboardingScreenIntent, NavigationIntent

    data object NavigateToSignUp :
        OnboardingScreenIntent, NavigationIntent

    data object OnSignInDismissed :
        OnboardingScreenIntent

    data class OnSignInError(val error: String) :
        OnboardingScreenIntent, SnackbarVisuals by DefaultSnackBarVisuals(error)
}
