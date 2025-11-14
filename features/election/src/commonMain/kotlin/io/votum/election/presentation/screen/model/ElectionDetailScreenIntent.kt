/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.presentation.screen.model

sealed interface ElectionDetailScreenIntent {
    data class LoadElectionDetails(val electionId: String) : ElectionDetailScreenIntent
    data object NavigateBack : ElectionDetailScreenIntent
    data object NavigateToVoting : ElectionDetailScreenIntent
    data class ShowError(val message: String) : ElectionDetailScreenIntent
}
