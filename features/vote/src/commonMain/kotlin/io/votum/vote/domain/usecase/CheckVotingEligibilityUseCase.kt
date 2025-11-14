/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.domain.usecase

import io.votum.vote.domain.model.VotingStatus
import io.votum.vote.domain.repository.VoteRepository
import org.koin.core.annotation.Factory

@Factory
class CheckVotingEligibilityUseCase(
    private val repository: VoteRepository
) {
    suspend operator fun invoke(
        electionId: String,
        voterId: String
    ): Result<VotingStatus> {
        return repository.checkVotingEligibility(electionId, voterId)
    }
}
