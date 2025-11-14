/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.domain.repository

import io.votum.identity.data.model.VerificationRequest
import io.votum.identity.data.model.VerificationResponse

interface IdentityRepository {
    suspend fun verifyIdentity(request: VerificationRequest): VerificationResponse
}
