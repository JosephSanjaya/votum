/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.onboarding.presentation.screen

import io.votum.core.presentation.utils.BaseViewModel
import io.votum.core.presentation.utils.VotumDispatchers
import io.votum.core.presentation.utils.getOrElseRethrowCancellation
import io.votum.onboarding.data.TestRepository
import io.votum.onboarding.presentation.screen.model.OnboardingScreenIntent
import io.votum.onboarding.presentation.screen.model.OnboardingScreenSideEffect
import io.votum.onboarding.presentation.screen.model.OnboardingScreenState
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class OnboardingScreenViewModel(
    private val testRepository: TestRepository,
    private val votumDispatchers: VotumDispatchers
) : BaseViewModel<OnboardingScreenState, OnboardingScreenSideEffect>(
    OnboardingScreenState()
) {

    override fun onIntent(intent: Any) {
        when (intent) {
            OnboardingScreenIntent.OnSignInClicked -> intent {
                reduce { state.copy(isLoading = true, result = null) }
                val result = withContext(votumDispatchers.io) {
                    testRepository.runCatching {
                        getTest()
                    }.getOrElseRethrowCancellation {
                        sendIntent(OnboardingScreenIntent.OnSignInError(it.message.orEmpty()))
                        null
                    }
                }
                reduce { state.copy(isLoading = false, result = result) }
            }

            OnboardingScreenIntent.NavigateToSignUp -> intent {
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
