/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.presentation.screen.model

import io.votum.core.presentation.navigation.NavigationIntent

sealed interface ElectionDetailScreenIntent : NavigationIntent {
    data class LoadElectionDetails(val electionId: String) : ElectionDetailScreenIntent
    data object NavigateBack : ElectionDetailScreenIntent
    data class NavigateToVoting(val electionId: String) : ElectionDetailScreenIntent
    data class ShowError(val message: String) : ElectionDetailScreenIntent
}
