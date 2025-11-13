package io.votum.onboarding.presentation.screen.model

import io.votum.core.presentation.navigation.NavigationIntent

sealed interface OnboardingScreenIntent {
    data object ToggleSignInSheet :
        OnboardingScreenIntent

    data object NavigateToSignUp :
        OnboardingScreenIntent, NavigationIntent

    data object NavigateToSignIn :
        OnboardingScreenIntent, NavigationIntent

    data object OnSignInDismissed :
        OnboardingScreenIntent
}
