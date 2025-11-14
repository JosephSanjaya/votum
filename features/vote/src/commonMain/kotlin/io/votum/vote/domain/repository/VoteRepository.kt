/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.domain.repository

import io.votum.vote.domain.model.VoteReceipt
import io.votum.vote.domain.model.VoteVerification
import io.votum.vote.domain.model.VotingStatus

interface VoteRepository {
    suspend fun checkVotingEligibility(
        electionId: String,
        voterId: String
    ): Result<VotingStatus>

    suspend fun castVote(
        electionId: String,
        voterId: String,
        candidateId: String,
        voterSignature: String
    ): Result<VoteReceipt>

    suspend fun verifyVote(
        transactionHash: String
    ): Result<VoteVerification>

    suspend fun getVoteReceiptById(
        voteId: String,
        voterId: String
    ): Result<VoteReceipt>

    suspend fun saveReceiptLocally(receipt: VoteReceipt)
    suspend fun getLocalReceipts(): List<VoteReceipt>
}
