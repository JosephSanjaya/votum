/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.data.remote

import io.votum.registration.data.model.RegistrationRequest
import io.votum.registration.data.model.RegistrationResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface RegistrationApi {
    @POST("api/auth/register")
    suspend fun registerVoter(@Body request: RegistrationRequest): RegistrationResponse
}
