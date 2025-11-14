/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.presentation.screen.model

import io.votum.election.domain.model.Election
import io.votum.election.domain.model.ElectionStatus
import io.votum.election.domain.model.Pagination

data class ElectionListScreenState(
    val elections: List<Election> = emptyList(),
    val pagination: Pagination? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedStatusFilter: ElectionStatus? = null,
    val error: String? = null,
    val isOffline: Boolean = false
)
