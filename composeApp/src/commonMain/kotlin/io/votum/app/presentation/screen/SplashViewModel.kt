package io.votum.app.presentation.screen

import io.votum.core.presentation.navigation.NavigationEvent
import io.votum.core.presentation.utils.BaseViewModel
import io.votum.core.presentation.utils.OnCreateHandler
import kotlinx.coroutines.delay
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.milliseconds

data class SplashScreenUiState(val title: String = "")

sealed interface SplashScreenUiEvent {
    data object NavigateToOnBoarding : SplashScreenUiEvent, NavigationEvent
}

@KoinViewModel
class SplashScreenViewModel :
    BaseViewModel<SplashScreenUiState, SplashScreenUiEvent>(
        initialState = SplashScreenUiState("Votum"),
        onCreateHandler = OnCreateHandler.create {
            intent {
                delay(100.milliseconds)
                onEvent(SplashScreenUiEvent.NavigateToOnBoarding)
            }
        }
    )
