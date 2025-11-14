/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.screen.model

import io.votum.core.presentation.navigation.NavigationIntent

sealed interface VotingScreenIntent : NavigationIntent {
    data class LoadVotingStatus(val electionId: String, val voterId: String) : VotingScreenIntent
    data class SelectCandidate(val candidateId: String) : VotingScreenIntent
    data object ShowConfirmation : VotingScreenIntent
    data object DismissConfirmation : VotingScreenIntent
    data class ConfirmVote(val privateKey: String) : VotingScreenIntent
    data class NavigateToReceipt(val voteId: String, val candidateName: String) : VotingScreenIntent
    data object DismissError : VotingScreenIntent
    data class ShowError(val message: String) : VotingScreenIntent
}
