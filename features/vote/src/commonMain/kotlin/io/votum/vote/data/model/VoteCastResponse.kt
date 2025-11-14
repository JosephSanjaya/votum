/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoteCastResponse(
    val success: Boolean,
    val data: VoteCastData?,
    val message: String,
    val timestamp: String,
    val requestId: String,
    val errors: List<ApiError>? = null
)

@Serializable
data class VoteCastData(
    val voteId: String,
    val electionId: String,
    val candidateId: String,
    val transactionHash: String,
    val blockNumber: String,
    val timestamp: String,
    val verificationCode: String
)

@Serializable
data class ApiError(
    val field: String,
    val message: String,
    val code: String
)
