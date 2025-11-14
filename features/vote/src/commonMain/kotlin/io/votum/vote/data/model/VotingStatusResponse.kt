/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VotingStatusResponse(
    val success: Boolean,
    val data: VotingStatusData?,
    val message: String,
    val timestamp: String,
    val requestId: String
)

@Serializable
data class VotingStatusData(
    val electionId: String,
    val isVotingActive: Boolean,
    val totalVotesCast: Int,
    val voterHasVoted: Boolean,
    val remainingTime: Long,
    val votingProgress: Double
)
