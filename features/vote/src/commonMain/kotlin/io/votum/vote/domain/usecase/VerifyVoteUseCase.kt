/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.domain.usecase

import io.votum.vote.domain.model.VoteVerification
import io.votum.vote.domain.repository.VoteRepository
import org.koin.core.annotation.Factory

@Factory
class VerifyVoteUseCase(
    private val repository: VoteRepository
) {
    suspend operator fun invoke(
        transactionHash: String
    ): Result<VoteVerification> {
        return repository.verifyVote(transactionHash)
    }
}
