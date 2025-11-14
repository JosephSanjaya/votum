/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.app.presentation.navigation.handler

import androidx.navigation.NavController
import io.votum.app.presentation.navigation.NavigationEventHandler
import io.votum.core.presentation.navigation.NavigationIntent
import io.votum.result.presentation.screen.LiveResult
import io.votum.result.presentation.screen.Result
import io.votum.result.presentation.screen.model.LiveResultScreenIntent
import io.votum.result.presentation.screen.model.ResultScreenIntent
import org.koin.core.annotation.Factory

@Factory
class ResultEventHandler : NavigationEventHandler() {

    override fun canHandle(event: NavigationIntent): Boolean {
        return event is ResultScreenIntent.NavigateBack ||
            event is LiveResultScreenIntent.NavigateBack
    }

    override fun navigate(navController: NavController, event: NavigationIntent) {
        when (event) {
            ResultScreenIntent.NavigateBack -> navController.navigateUp()
            LiveResultScreenIntent.NavigateBack -> navController.navigateUp()
        }
    }
}

fun NavController.navigateToResults(electionId: String) {
    navigate(Result(electionId))
}

fun NavController.navigateToLiveResults(electionId: String) {
    navigate(LiveResult(electionId))
}
