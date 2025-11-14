/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.data.repository

import de.jensklingenberg.ktorfit.Ktorfit
import io.votum.election.data.mapper.toDomain
import io.votum.election.data.remote.ElectionApi
import io.votum.election.data.remote.createElectionApi
import io.votum.election.domain.model.ElectionDetail
import io.votum.election.domain.model.PaginatedElections
import io.votum.election.domain.repository.ElectionRepository
import org.koin.core.annotation.Single

@Single
class ElectionRepositoryImpl(
    ktorfit: Ktorfit
) : ElectionRepository {

    private val api: ElectionApi = ktorfit.createElectionApi()

    override suspend fun getElections(
        page: Int,
        limit: Int
    ): Result<PaginatedElections> {
        return try {
            val response = api.getElections(page, limit)
            if (response.success) {
                Result.success(response.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getElectionDetails(
        electionId: String
    ): Result<ElectionDetail> {
        return try {
            val response = api.getElectionDetails(electionId)
            if (response.success) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchElections(
        query: String,
        page: Int
    ): Result<PaginatedElections> {
        return try {
            val response = api.getElections(page, 10)
            if (response.success) {
                val filtered = response.data.filter {
                    it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
                }
                val filteredResponse = response.copy(data = filtered)
                Result.success(filteredResponse.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
