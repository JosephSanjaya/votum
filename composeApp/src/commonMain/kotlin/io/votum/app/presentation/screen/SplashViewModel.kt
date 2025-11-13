package io.votum.app.presentation.screen

import io.votum.core.presentation.navigation.NavigationIntent
import io.votum.core.presentation.utils.BaseViewModel
import kotlinx.coroutines.delay
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.milliseconds

data class SplashScreenUiState(val title: String = "")
sealed interface SplashScreenIntent {
    data object NavigateToOnBoarding : SplashScreenIntent, NavigationIntent
}

@KoinViewModel
class SplashScreenViewModel :
    BaseViewModel<SplashScreenUiState, Unit>(
        initialState = SplashScreenUiState("Votum"),
        onCreate = {
            delay(100.milliseconds)
            sendIntent(SplashScreenIntent.NavigateToOnBoarding)
        }
    )
