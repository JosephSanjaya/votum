/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.domain.usecase

import io.votum.result.data.repository.ResultRepositoryImpl
import io.votum.result.domain.model.LiveResults
import org.koin.core.annotation.Factory

@Factory
class GetLiveResultsUseCase(
    private val repository: ResultRepositoryImpl
) {
    suspend operator fun invoke(electionId: String): Result<LiveResults> {
        return repository.getLive(electionId)
    }
}
