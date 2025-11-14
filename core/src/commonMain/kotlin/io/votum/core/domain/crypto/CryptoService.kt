/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.core.domain.crypto

interface CryptoService {

    suspend fun signVote(
        electionId: String,
        candidateId: String,
        privateKey: String
    ): Result<String>

    suspend fun generateKeyPair(): Result<KeyPair>

    suspend fun clearSensitiveData()
}

data class KeyPair(
    val publicKey: String,
    val privateKey: String
)
