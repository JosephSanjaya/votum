/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.onboarding.presentation.screen

import io.votum.core.presentation.utils.BaseViewModel
import io.votum.onboarding.presentation.screen.model.OnboardingScreenIntent
import io.votum.onboarding.presentation.screen.model.OnboardingScreenSideEffect
import io.votum.onboarding.presentation.screen.model.OnboardingScreenState
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class OnboardingScreenViewModel : BaseViewModel<OnboardingScreenState, OnboardingScreenSideEffect>(
    OnboardingScreenState()
) {

    override fun onIntent(intent: Any) {
        when (intent) {
            OnboardingScreenIntent.NavigateToSignUp, OnboardingScreenIntent.NavigateToSignIn -> intent {
                reduce { state.copy(isShownSignInSheet = false) }
            }

            OnboardingScreenIntent.OnSignInDismissed -> intent {
                reduce { state.copy(isShownSignInSheet = false) }
            }

            OnboardingScreenIntent.ToggleSignInSheet -> intent {
                reduce { state.copy(isShownSignInSheet = !state.isShownSignInSheet) }
            }

            else -> Unit
        }
    }
}
