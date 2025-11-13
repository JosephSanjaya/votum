/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.onboarding.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.votum.core.presentation.theme.VotumTheme
import io.votum.onboarding.presentation.screen.model.OnboardingScreenIntent
import io.votum.onboarding.presentation.screen.model.OnboardingScreenState
import kotlinx.collections.immutable.PersistentList
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OnboardBody(
    contents: PersistentList<OnboardingScreenState.OnboardContent>,
    modifier: Modifier = Modifier,
    shouldShowSignInSheet: Boolean = false,
    pagerState: PagerState = rememberPagerState(0) { contents.size },
    onIntent: (OnboardingScreenIntent) -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        HorizontalPager(
            pagerState,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            OnboardStep(
                illustrationRes = contents[index].illustrationRes,
                titleRes = contents[index].titleRes,
                descriptionRes = contents[index].descriptionRes
            )
        }
        SignInSheet(
            isShown = shouldShowSignInSheet,
            modifier = Modifier.align(Alignment.BottomStart),
            onSignInClick = {
                onIntent(OnboardingScreenIntent.OnSignInClicked)
            },
            onSignUpClick = {
                onIntent(OnboardingScreenIntent.NavigateToSignUp)
            },
            onDismissRequest = {
                onIntent(OnboardingScreenIntent.OnSignInDismissed)
            }
        )
    }
}

@Composable
@Preview
private fun OnboardBodyPreview() {
    VotumTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            OnboardBody(
                OnboardingScreenState.OnboardContent.default()
            )
        }
    }
}
