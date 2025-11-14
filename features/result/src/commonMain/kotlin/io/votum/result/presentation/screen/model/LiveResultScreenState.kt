/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.screen.model

import io.votum.result.domain.model.LiveResults

data class LiveResultScreenState(
    val isLoading: Boolean = false,
    val liveResults: LiveResults? = null,
    val error: String? = null,
    val autoRefreshEnabled: Boolean = true,
    val lastRefreshTime: String? = null
)
