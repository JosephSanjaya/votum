/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.domain.usecase

import io.votum.election.domain.model.ElectionDetail
import io.votum.election.domain.repository.ElectionRepository
import org.koin.core.annotation.Single

@Single
class GetElectionDetailsUseCase(
    private val repository: ElectionRepository
) {
    suspend operator fun invoke(
        electionId: String
    ): Result<ElectionDetail> {
        return repository.getElectionDetails(electionId)
    }
}
