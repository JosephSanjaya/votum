/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LiveResultsResponse(
    val success: Boolean,
    val data: LiveResultsData?,
    val message: String,
    val timestamp: String,
    val requestId: String
)

@Serializable
data class LiveResultsData(
    val electionId: String,
    val currentResults: ElectionResultsData,
    val lastUpdated: String,
    val isLive: Boolean,
    val updateInterval: Int
)
