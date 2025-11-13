/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.onboarding.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.votum.core.presentation.preview.PositionPreviewProvider
import io.votum.core.presentation.theme.LocalSnackBarHost
import io.votum.core.presentation.theme.VotumTheme
import io.votum.onboarding.presentation.component.OnboardBody
import io.votum.onboarding.presentation.component.OnboardFooter
import io.votum.onboarding.presentation.component.OnboardHeader
import io.votum.onboarding.presentation.screen.model.OnboardingScreenIntent
import io.votum.onboarding.presentation.screen.model.OnboardingScreenState
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingScreenViewModel = koinViewModel()
) {
    val state = viewModel.collectAsState().value
    val pagerState: PagerState = rememberPagerState(0) { state.onboardContent.size }
    OnboardingScreenContent(
        uiState = state,
        modifier = modifier,
        onIntent = viewModel::sendIntent,
        pagerState = pagerState
    )
}

@Composable
private fun OnboardingScreenContent(
    uiState: OnboardingScreenState,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(0) { uiState.onboardContent.size },
    onIntent: (OnboardingScreenIntent) -> Unit = {}
) {
    val snackbarHostState = LocalSnackBarHost.current
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        topBar = {
            OnboardHeader(
                pagerState.pageCount,
                pagerState.currentPage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .statusBarsPadding(),
                onSkipClicked = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.pageCount - 1)
                    }
                }
            )
        },
        bottomBar = {
            val lastStep = pagerState.currentPage == uiState.onboardContent.lastIndex
            OnboardFooter(
                lastStep,
                modifier = Modifier
                    .navigationBarsPadding()
                    .statusBarsPadding()
            ) {
                if (lastStep) {
                    onIntent(OnboardingScreenIntent.ToggleSignInSheet)
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        }
    ) { padding ->
        OnboardBody(
            contents = uiState.onboardContent,
            modifier = Modifier.padding(padding),
            pagerState = pagerState,
            shouldShowSignInSheet = uiState.isShownSignInSheet,
            onIntent = onIntent
        )
    }
}

@Serializable
object Onboarding

@Composable
@Preview(showBackground = true)
private fun OnboardingScreenPreview(
    @PreviewParameter(PositionPreviewProvider::class) currentPage: Int
) {
    VotumTheme {
        val uiState =
            OnboardingScreenState()
        OnboardingScreenContent(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            uiState = uiState,
            pagerState = rememberPagerState(currentPage) { uiState.onboardContent.size },
        )
    }
}
