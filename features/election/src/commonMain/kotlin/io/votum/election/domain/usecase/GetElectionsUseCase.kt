/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.domain.usecase

import io.votum.election.domain.model.PaginatedElections
import io.votum.election.domain.repository.ElectionRepository
import org.koin.core.annotation.Single

@Single
class GetElectionsUseCase(
    private val repository: ElectionRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 10
    ): Result<PaginatedElections> {
        return repository.getElections(page, limit)
    }
}
