/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.presentation.screen.model

import io.votum.election.domain.model.ElectionDetail

data class ElectionDetailScreenState(
    val election: ElectionDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
