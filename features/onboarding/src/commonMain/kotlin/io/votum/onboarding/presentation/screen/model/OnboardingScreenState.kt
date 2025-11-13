package io.votum.onboarding.presentation.screen.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
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

data class OnboardingScreenState(
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
