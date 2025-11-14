/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ElectionListResponse(
    val success: Boolean,
    val data: List<ElectionDto>,
    val pagination: PaginationDto,
    val message: String,
    val timestamp: String,
    val requestId: String
)

@Serializable
data class ElectionDetailResponse(
    val success: Boolean,
    val data: ElectionDetailDto,
    val message: String,
    val timestamp: String,
    val requestId: String
)

@Serializable
data class CandidateListResponse(
    val success: Boolean,
    val data: List<CandidateDto>,
    val message: String,
    val timestamp: String,
    val requestId: String
)
