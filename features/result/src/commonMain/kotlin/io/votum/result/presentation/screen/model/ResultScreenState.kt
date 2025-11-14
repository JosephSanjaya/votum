/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.screen.model

import io.votum.result.domain.model.ElectionResults

data class ResultScreenState(
    val isLoading: Boolean = false,
    val results: ElectionResults? = null,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val lastUpdated: String? = null
)
