/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.core.domain.storage

interface SecureStorage {

    suspend fun storePrivateKey(key: String, value: String): Result<Unit>

    suspend fun retrievePrivateKey(key: String): Result<String?>

    suspend fun deletePrivateKey(key: String): Result<Unit>

    suspend fun clearAll(): Result<Unit>
}
