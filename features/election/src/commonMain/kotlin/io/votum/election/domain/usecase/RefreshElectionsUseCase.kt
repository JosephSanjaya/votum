/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.domain.usecase

import io.votum.election.domain.model.PaginatedElections
import io.votum.election.domain.repository.ElectionRepository
import org.koin.core.annotation.Single

@Single
class RefreshElectionsUseCase(
    private val repository: ElectionRepository
) {
    suspend operator fun invoke(): Result<PaginatedElections> {
        return repository.getElections(page = 1, limit = 10)
    }
}
