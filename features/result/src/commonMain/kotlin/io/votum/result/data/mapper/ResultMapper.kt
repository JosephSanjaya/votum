/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.data.mapper

import io.votum.result.data.model.CandidateResultResponse
import io.votum.result.data.model.ElectionResultsData
import io.votum.result.data.model.LiveResultsData
import io.votum.result.domain.model.CandidateResult
import io.votum.result.domain.model.ElectionResults
import io.votum.result.domain.model.LiveResults
import kotlinx.collections.immutable.toPersistentList
import org.koin.core.annotation.Single

@Single
class ResultMapper {

    fun toDomain(data: ElectionResultsData): ElectionResults {
        return ElectionResults(
            electionId = data.electionId,
            totalVotesCast = data.totalVotesCast,
            totalEligibleVoters = data.totalEligibleVoters,
            turnoutPercentage = data.turnoutPercentage,
            candidateResults = data.candidateResults.map { toCandidateResult(it) }
                .toPersistentList(),
            winner = data.candidateResults.find { it.isWinner }?.let { toCandidateResult(it) },
            isFinalized = data.isFinalized,
            finalizedAt = data.finalizedAt,
            blockchainProof = data.blockchainProof
        )
    }

    fun toLiveDomain(data: LiveResultsData): LiveResults {
        return LiveResults(
            electionId = data.electionId,
            currentResults = toDomain(data.currentResults),
            lastUpdated = data.lastUpdated,
            isLive = data.isLive,
            updateInterval = data.updateInterval
        )
    }

    private fun toCandidateResult(response: CandidateResultResponse): CandidateResult {
        return CandidateResult(
            candidateId = response.candidateId,
            candidateName = response.candidateName,
            voteCount = response.voteCount,
            votePercentage = response.votePercentage,
            isWinner = response.isWinner
        )
    }
}
