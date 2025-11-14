/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.domain.usecase

import io.votum.election.domain.model.PaginatedElections
import io.votum.election.domain.repository.ElectionRepository
import org.koin.core.annotation.Single

@Single
class SearchElectionsUseCase(
    private val repository: ElectionRepository
) {
    suspend operator fun invoke(
        query: String,
        page: Int = 1
    ): Result<PaginatedElections> {
        if (query.isBlank()) {
            return repository.getElections(page)
        }
        return repository.searchElections(query, page)
    }
}
