/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.domain.model

data class VotingStatus(
    val electionId: String,
    val isVotingActive: Boolean,
    val totalVotesCast: Int,
    val voterHasVoted: Boolean,
    val remainingTime: Long,
    val votingProgress: Double,
    val candidates: List<Candidate> = emptyList()
)
