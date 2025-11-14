/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.app.presentation.navigation.handler

import androidx.navigation.NavController
import io.votum.app.presentation.navigation.NavigationEventHandler
import io.votum.core.presentation.navigation.NavigationIntent
import io.votum.election.presentation.screen.model.ElectionDetailScreenIntent
import io.votum.vote.presentation.screen.Voting
import org.koin.core.annotation.Factory

@Factory
class ElectionEventHandler : NavigationEventHandler() {

    override fun canHandle(event: NavigationIntent): Boolean {
        return event is ElectionDetailScreenIntent.NavigateToVoting ||
            event is ElectionDetailScreenIntent.NavigateBack
    }

    override fun navigate(navController: NavController, event: NavigationIntent) {
        when (event) {
            is ElectionDetailScreenIntent.NavigateToVoting -> navController.navigate(
                Voting(electionId = event.electionId)
            )
            ElectionDetailScreenIntent.NavigateBack -> navController.navigateUp()
        }
    }
}
