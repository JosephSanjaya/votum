/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.presentation.screen.model

import io.votum.core.presentation.navigation.NavigationIntent
import io.votum.election.domain.model.ElectionStatus

sealed interface ElectionListScreenIntent {
    data object LoadElections : ElectionListScreenIntent
    data object LoadNextPage : ElectionListScreenIntent
    data object RefreshElections : ElectionListScreenIntent
    data class SearchElections(val query: String) : ElectionListScreenIntent
    data class FilterByStatus(val status: ElectionStatus?) : ElectionListScreenIntent
    data class NavigateToElectionDetail(val electionId: String) : ElectionListScreenIntent, NavigationIntent
    data class ShowError(val message: String) : ElectionListScreenIntent
}
