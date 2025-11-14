/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.domain.model

data class VoteVerification(
    val isValid: Boolean,
    val transactionHash: String,
    val blockNumber: String,
    val timestamp: String,
    val voterPublicKey: String,
    val candidateIndex: Int,
    val verificationProof: String
)
