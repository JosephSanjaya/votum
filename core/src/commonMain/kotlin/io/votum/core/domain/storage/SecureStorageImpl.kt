/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.core.domain.storage

import com.russhwolf.settings.set
import io.votum.core.data.LocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class SecureStorageImpl(
    private val localDataSource: LocalDataSource
) : SecureStorage {

    private val secureKeyPrefix = "secure_key_"

    override suspend fun storePrivateKey(key: String, value: String): Result<Unit> =
        withContext(Dispatchers.Default) {
            try {
                localDataSource["$secureKeyPrefix$key"] = value
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(SecureStorageException("Failed to store private key", e))
            }
        }

    override suspend fun retrievePrivateKey(key: String): Result<String?> =
        withContext(Dispatchers.Default) {
            try {
                val value = localDataSource.getStringOrNull("$secureKeyPrefix$key")
                Result.success(value)
            } catch (e: Exception) {
                Result.failure(SecureStorageException("Failed to retrieve private key", e))
            }
        }

    override suspend fun deletePrivateKey(key: String): Result<Unit> =
        withContext(Dispatchers.Default) {
            try {
                localDataSource.remove("$secureKeyPrefix$key")
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(SecureStorageException("Failed to delete private key", e))
            }
        }

    override suspend fun clearAll(): Result<Unit> =
        withContext(Dispatchers.Default) {
            try {
                localDataSource.keys
                    .filter { it.startsWith(secureKeyPrefix) }
                    .forEach { localDataSource.remove(it) }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(SecureStorageException("Failed to clear all keys", e))
            }
        }
}

class SecureStorageException(message: String, cause: Throwable? = null) : Exception(message, cause)
