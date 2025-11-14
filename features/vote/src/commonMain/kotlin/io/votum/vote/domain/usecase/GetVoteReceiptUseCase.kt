/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.domain.usecase

import io.votum.vote.domain.model.VoteReceipt
import io.votum.vote.domain.repository.VoteRepository
import org.koin.core.annotation.Factory

@Factory
class GetVoteReceiptUseCase(
    private val repository: VoteRepository
) {
    suspend operator fun invoke(
        voteId: String,
        voterId: String
    ): Result<VoteReceipt> {
        return repository.getVoteReceiptById(voteId, voterId)
    }
}
