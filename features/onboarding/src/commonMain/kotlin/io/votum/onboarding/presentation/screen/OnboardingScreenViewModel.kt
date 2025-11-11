/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.onboarding.presentation.screen

import androidx.compose.material3.SnackbarVisuals
import io.votum.core.presentation.component.DefaultSnackBarVisuals
import io.votum.core.presentation.utils.BaseViewModel
import io.votum.onboarding.data.TestRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import votum.features.onboarding.generated.resources.Res
import votum.features.onboarding.generated.resources.ic_onboarding_step1
import votum.features.onboarding.generated.resources.ic_onboarding_step2
import votum.features.onboarding.generated.resources.ic_onboarding_step3
import votum.features.onboarding.generated.resources.subtitle_onboard_step1
import votum.features.onboarding.generated.resources.subtitle_onboard_step2
import votum.features.onboarding.generated.resources.subtitle_onboard_step3
import votum.features.onboarding.generated.resources.title_onboard_step1
import votum.features.onboarding.generated.resources.title_onboard_step2
import votum.features.onboarding.generated.resources.title_onboard_step3

data class OnboardingScreenUiState(
    val isLoading: Boolean = false,
    val result: String? = null,
    val onboardContent: PersistentList<OnboardContent> = OnboardContent.Companion.default(),
    val isShownSignInSheet: Boolean = false,
) {
    data class OnboardContent(
        val illustrationRes: DrawableResource,
        val titleRes: StringResource,
        val descriptionRes: StringResource
    ) {
        companion object {
            fun default() = persistentListOf(
                OnboardContent(
                    illustrationRes = Res.drawable.ic_onboarding_step1,
                    titleRes = Res.string.title_onboard_step1,
                    descriptionRes = Res.string.subtitle_onboard_step1
                ),
                OnboardContent(
                    illustrationRes = Res.drawable.ic_onboarding_step2,
                    titleRes = Res.string.title_onboard_step2,
                    descriptionRes = Res.string.subtitle_onboard_step2
                ),
                OnboardContent(
                    illustrationRes = Res.drawable.ic_onboarding_step3,
                    titleRes = Res.string.title_onboard_step3,
                    descriptionRes = Res.string.subtitle_onboard_step3
                ),
            )
        }
    }
}

sealed interface OnboardingScreenUiEvent {
    data object ToggleSignInSheet :
        OnboardingScreenUiEvent
    data object OnSignInClicked :
        OnboardingScreenUiEvent
    data object OnSignInDismissed :
        OnboardingScreenUiEvent
    data class SignInError(override val message: String) :
        OnboardingScreenUiEvent,
        SnackbarVisuals by DefaultSnackBarVisuals(
            message
        )
}

@KoinViewModel
class OnboardingScreenViewModel(
    private val testRepository: TestRepository
) : BaseViewModel<OnboardingScreenUiState, OnboardingScreenUiEvent>(
    OnboardingScreenUiState()
) {
    override fun onEvent(event: OnboardingScreenUiEvent) {
        super.onEvent(event)
        when (event) {
            OnboardingScreenUiEvent.OnSignInClicked -> intent {
                reduce { state.copy(isLoading = true, result = null) }
                val result = safeIoCall {
                    testRepository.getTest()
                }
                reduce { state.copy(isLoading = false, result = result) }
            }

            OnboardingScreenUiEvent.OnSignInDismissed -> intent {
                reduce { state.copy(isShownSignInSheet = false) }
            }

            OnboardingScreenUiEvent.ToggleSignInSheet -> intent {
                reduce { state.copy(isShownSignInSheet = !state.isShownSignInSheet) }
            }

            else -> Unit
        }
    }
}
