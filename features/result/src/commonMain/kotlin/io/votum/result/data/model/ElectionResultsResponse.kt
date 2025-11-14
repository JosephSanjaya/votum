/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ElectionResultsResponse(
    val success: Boolean,
    val data: ElectionResultsData?,
    val message: String,
    val timestamp: String,
    val requestId: String
)

@Serializable
data class ElectionResultsData(
    val electionId: String,
    val totalVotesCast: Int,
    val totalEligibleVoters: Int,
    val turnoutPercentage: Double,
    val candidateResults: List<CandidateResultResponse>,
    val winningCandidate: String?,
    val isFinalized: Boolean,
    val finalizedAt: String?,
    val blockchainProof: String
)

@Serializable
data class CandidateResultResponse(
    val candidateId: String,
    val candidateName: String,
    val voteCount: Int,
    val votePercentage: Double,
    val isWinner: Boolean
)
