/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.data.repository

import de.jensklingenberg.ktorfit.Ktorfit
import io.votum.vote.data.mapper.toDomain
import io.votum.vote.data.model.VoteCastRequest
import io.votum.vote.data.remote.VoteApi
import io.votum.vote.data.remote.createVoteApi
import io.votum.vote.domain.model.VoteReceipt
import io.votum.vote.domain.model.VoteVerification
import io.votum.vote.domain.model.VotingStatus
import io.votum.vote.domain.repository.VoteRepository
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class VoteRepositoryImpl(
    ktorfit: Ktorfit
) : VoteRepository {

    private val api: VoteApi = ktorfit.createVoteApi()
    private val json = Json { prettyPrint = true }
    private val localReceipts = mutableListOf<VoteReceipt>()

    override suspend fun checkVotingEligibility(
        electionId: String,
        voterId: String
    ): Result<VotingStatus> {
        return try {
            val response = api.getVotingStatus(electionId, voterId)
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun castVote(
        electionId: String,
        voterId: String,
        candidateId: String,
        voterSignature: String
    ): Result<VoteReceipt> {
        return try {
            val request = VoteCastRequest(
                electionId = electionId,
                voterId = voterId,
                candidateId = candidateId,
                voterSignature = voterSignature
            )
            val response = api.castVote(request)
            if (response.success && response.data != null) {
                val receipt = response.data.toDomain()
                saveReceiptLocally(receipt)
                Result.success(receipt)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyVote(
        transactionHash: String
    ): Result<VoteVerification> {
        return try {
            val response = api.verifyVote(transactionHash)
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVoteReceiptById(
        voteId: String,
        voterId: String
    ): Result<VoteReceipt> {
        return try {
            val localReceipt = localReceipts.find { it.voteId == voteId }
            if (localReceipt != null) {
                return Result.success(localReceipt)
            }

            val response = api.getVoteReceipt(voteId, voterId)
            if (response.success && response.data != null) {
                val receipt = response.data.toDomain()
                saveReceiptLocally(receipt)
                Result.success(receipt)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveReceiptLocally(receipt: VoteReceipt) {
        if (!localReceipts.any { it.voteId == receipt.voteId }) {
            localReceipts.add(receipt)
        }
    }

    override suspend fun getLocalReceipts(): List<VoteReceipt> {
        return localReceipts.toList()
    }
}
