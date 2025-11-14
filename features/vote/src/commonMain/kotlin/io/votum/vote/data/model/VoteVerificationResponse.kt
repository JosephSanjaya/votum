/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoteVerificationResponse(
    val success: Boolean,
    val data: VoteVerificationData?,
    val message: String,
    val timestamp: String,
    val requestId: String
)

@Serializable
data class VoteVerificationData(
    val isValid: Boolean,
    val transactionHash: String,
    val blockNumber: String,
    val timestamp: String,
    val voterPublicKey: String,
    val candidateIndex: Int,
    val verificationProof: String
)
