/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.data.repository

import de.jensklingenberg.ktorfit.Ktorfit
import io.votum.result.data.mapper.ResultMapper
import io.votum.result.data.remote.ResultApi
import io.votum.result.data.remote.createResultApi
import io.votum.result.domain.model.ElectionResults
import io.votum.result.domain.model.LiveResults
import io.votum.result.domain.repository.ResultRepository
import org.koin.core.annotation.Single

@Single
class ResultRepositoryImpl(
    ktorfit: Ktorfit,
    private val resultMapper: ResultMapper
) : ResultRepository {

    private val api: ResultApi = ktorfit.createResultApi()

    suspend fun getResults(electionId: String): Result<ElectionResults> {
        return try {
            val response = api.getElectionResults(electionId)
            if (response.success && response.data != null) {
                Result.success(resultMapper.toDomain(response.data))
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLive(electionId: String): Result<LiveResults> {
        return try {
            val response = api.getLiveResults(electionId)
            if (response.success && response.data != null) {
                Result.success(resultMapper.toLiveDomain(response.data))
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getElectionResults(electionId: String) =
        api.getElectionResults(electionId)

    override suspend fun getLiveResults(electionId: String) =
        api.getLiveResults(electionId)
}
