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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.votum.core.presentation.component.rememberSnackBarHostState
import io.votum.core.presentation.preview.PositionPreviewProvider
import io.votum.core.presentation.theme.VotumTheme
import io.votum.onboarding.presentation.component.OnboardBody
import io.votum.onboarding.presentation.component.OnboardFooter
import io.votum.onboarding.presentation.component.OnboardHeader
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = rememberSnackBarHostState(),
    viewModel: OnboardingScreenViewModel = koinViewModel()
) {
    val state = viewModel.collectAsState().value
    val pagerState: PagerState = rememberPagerState(0) { state.onboardContent.size }
    OnboardingScreenContent(
        uiState = state,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onEvent = {
            viewModel.onEvent(it)
        },
        pagerState = pagerState
    )
}

@Composable
private fun OnboardingScreenContent(
    uiState: OnboardingScreenUiState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = rememberSnackBarHostState(),
    pagerState: PagerState = rememberPagerState(0) { uiState.onboardContent.size },
    onEvent: (OnboardingScreenUiEvent) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    onEvent(OnboardingScreenUiEvent.ToggleSignInSheet)
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
            onEvent = onEvent
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
            OnboardingScreenUiState()
        OnboardingScreenContent(
            modifier = Modifier.background(Color.White),
            uiState = uiState,
            pagerState = rememberPagerState(currentPage) { uiState.onboardContent.size },
        )
    }
}
