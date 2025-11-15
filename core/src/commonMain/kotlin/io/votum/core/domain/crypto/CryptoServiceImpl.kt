/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.core.domain.crypto

import io.votum.core.domain.storage.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Single
class CryptoServiceImpl(
    private val secureStorage: SecureStorage
) : CryptoService {

    private val sensitiveDataCache = mutableMapOf<String, ByteArray>()

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun signVote(
        electionId: String,
        candidateId: String,
        privateKey: String
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            val messageToSign = buildVoteMessage(electionId, candidateId)
            val messageBytes = messageToSign.encodeToByteArray()

            val privateKeyBytes = decodePrivateKey(privateKey)

            val signature = signWithSR25519(messageBytes, privateKeyBytes)

            clearByteArray(privateKeyBytes)
            clearByteArray(messageBytes)

            val signatureBase64 = Base64.encode(signature)
            clearByteArray(signature)

            Result.success(signatureBase64)
        } catch (e: Exception) {
            Result.failure(CryptoException("Failed to sign vote: ${e.message}", e))
        }
    }

    override suspend fun generateKeyPair(): Result<KeyPair> = withContext(Dispatchers.Default) {
        try {
            val (publicKeyBytes, privateKeyBytes) = generateSR25519KeyPair()

            @OptIn(ExperimentalEncodingApi::class)
            val publicKey = Base64.encode(publicKeyBytes)

            @OptIn(ExperimentalEncodingApi::class)
            val privateKey = Base64.encode(privateKeyBytes)

            sensitiveDataCache["privateKey"] = privateKeyBytes

            clearByteArray(publicKeyBytes)

            Result.success(KeyPair(publicKey, privateKey))
        } catch (e: Exception) {
            Result.failure(CryptoException("Failed to generate key pair: ${e.message}", e))
        }
    }

    override suspend fun clearSensitiveData() = withContext(Dispatchers.Default) {
        sensitiveDataCache.values.forEach { clearByteArray(it) }
        sensitiveDataCache.clear()
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun decodePrivateKey(privateKey: String): ByteArray {
        return try {
            Base64.decode(privateKey)
        } catch (e: Exception) {
            throw CryptoException("Invalid private key format", e)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun buildVoteMessage(electionId: String, candidateId: String): String {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        return "VOTE:$electionId:$candidateId:$timestamp"
    }

    private fun signWithSR25519(message: ByteArray, privateKey: ByteArray): ByteArray {
        return simulateSR25519Signature(message, privateKey)
    }

    private fun generateSR25519KeyPair(): Pair<ByteArray, ByteArray> {
        return simulateSR25519KeyPairGeneration()
    }

    private fun simulateSR25519Signature(message: ByteArray, privateKey: ByteArray): ByteArray {
        val combinedData = message + privateKey
        val hash = combinedData.fold(0L) { acc, byte ->
            (acc * 31 + byte.toLong()) and 0xFFFFFFFFL
        }

        return ByteArray(64) { index ->
            val messageValue = message.getOrElse(index % message.size) { 0 }.toLong()
            ((hash shr (index % 32)) xor messageValue).toByte()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun simulateSR25519KeyPairGeneration(): Pair<ByteArray, ByteArray> {
        val seed = Clock.System.now().toEpochMilliseconds()

        val privateKey = ByteArray(32) { index ->
            ((seed shr (index % 8)) xor (index * 17L)).toByte()
        }

        val publicKey = ByteArray(32) { index ->
            ((privateKey[index].toLong() and 0xFF) xor (index * 23L)).toByte()
        }

        return Pair(publicKey, privateKey)
    }

    private fun clearByteArray(array: ByteArray) {
        array.fill(0)
    }
}

class CryptoException(message: String, cause: Throwable? = null) : Exception(message, cause)
