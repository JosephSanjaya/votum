/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.data.remote

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.votum.identity.data.model.VerificationRequest
import io.votum.identity.data.model.VerificationResponse

interface IdentityApi {
    @POST("api/auth/verify")
    suspend fun verifyIdentity(
        @Body request: VerificationRequest
    ): VerificationResponse
}
