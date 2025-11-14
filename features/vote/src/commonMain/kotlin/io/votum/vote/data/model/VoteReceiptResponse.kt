/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoteReceiptResponse(
    val success: Boolean,
    val data: VoteReceiptData?,
    val message: String,
    val timestamp: String,
    val requestId: String
)

@Serializable
data class VoteReceiptData(
    val voteId: String,
    val electionId: String,
    val candidateId: String,
    val transactionHash: String,
    val blockNumber: String,
    val timestamp: String,
    val verificationCode: String
)
