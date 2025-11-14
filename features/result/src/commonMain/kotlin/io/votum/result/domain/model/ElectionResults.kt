/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.domain.model

import kotlinx.collections.immutable.PersistentList

data class ElectionResults(
    val electionId: String,
    val totalVotesCast: Int,
    val totalEligibleVoters: Int,
    val turnoutPercentage: Double,
    val candidateResults: PersistentList<CandidateResult>,
    val winner: CandidateResult?,
    val isFinalized: Boolean,
    val finalizedAt: String?,
    val blockchainProof: String
)

data class CandidateResult(
    val candidateId: String,
    val candidateName: String,
    val voteCount: Int,
    val votePercentage: Double,
    val isWinner: Boolean
)

data class LiveResults(
    val electionId: String,
    val currentResults: ElectionResults,
    val lastUpdated: String,
    val isLive: Boolean,
    val updateInterval: Int
)
