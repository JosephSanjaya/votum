/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.app.presentation.navigation.handler

import androidx.navigation.NavController
import io.votum.app.presentation.navigation.NavigationEventHandler
import io.votum.core.presentation.navigation.NavigationIntent
import io.votum.vote.presentation.screen.VoteReceipt
import io.votum.vote.presentation.screen.model.VoteReceiptScreenIntent
import io.votum.vote.presentation.screen.model.VotingScreenIntent
import org.koin.core.annotation.Factory

@Factory
class VoteEventHandler : NavigationEventHandler() {

    override fun canHandle(event: NavigationIntent): Boolean {
        return event is VotingScreenIntent.NavigateToReceipt ||
            event is VoteReceiptScreenIntent.NavigateBack
    }

    override fun navigate(navController: NavController, event: NavigationIntent) {
        when (event) {
            is VotingScreenIntent.NavigateToReceipt -> navController.navigate(
                VoteReceipt(
                    voteId = event.voteId,
                    candidateName = event.candidateName
                )
            )
            VoteReceiptScreenIntent.NavigateBack -> navController.navigateUp()
        }
    }
}
