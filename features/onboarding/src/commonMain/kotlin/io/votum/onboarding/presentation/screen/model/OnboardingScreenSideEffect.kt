package io.votum.onboarding.presentation.screen.model

import androidx.compose.material3.SnackbarVisuals
import io.votum.core.presentation.component.DefaultSnackBarVisuals

sealed interface OnboardingScreenSideEffect {

    data class SignInError(override val message: String) :
        OnboardingScreenSideEffect,
        SnackbarVisuals by DefaultSnackBarVisuals(
            message
        )
}
