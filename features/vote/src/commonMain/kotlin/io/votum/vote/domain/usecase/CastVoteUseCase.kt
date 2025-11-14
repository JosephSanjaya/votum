/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.domain.usecase

import io.votum.core.domain.crypto.CryptoService
import io.votum.vote.domain.model.VoteReceipt
import io.votum.vote.domain.repository.VoteRepository
import org.koin.core.annotation.Factory

@Factory
class CastVoteUseCase(
    private val repository: VoteRepository,
    private val cryptoService: CryptoService
) {
    suspend operator fun invoke(
        electionId: String,
        voterId: String,
        candidateId: String,
        privateKey: String
    ): Result<VoteReceipt> {
        return cryptoService.signVote(
            electionId = electionId,
            candidateId = candidateId,
            privateKey = privateKey
        ).fold(
            onSuccess = { signature ->
                repository.castVote(
                    electionId = electionId,
                    voterId = voterId,
                    candidateId = candidateId,
                    voterSignature = signature
                ).also {
                    cryptoService.clearSensitiveData()
                }
            },
            onFailure = { error ->
                cryptoService.clearSensitiveData()
                Result.failure(error)
            }
        )
    }
}
