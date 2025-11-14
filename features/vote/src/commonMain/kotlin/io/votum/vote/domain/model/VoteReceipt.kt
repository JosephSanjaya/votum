/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.domain.model

data class VoteReceipt(
    val voteId: String,
    val electionId: String,
    val candidateId: String,
    val transactionHash: String,
    val blockNumber: String,
    val timestamp: String,
    val verificationCode: String
)
