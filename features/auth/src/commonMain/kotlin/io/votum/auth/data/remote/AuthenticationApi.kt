/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.auth.data.remote

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.votum.auth.data.model.LoginRequest
import io.votum.auth.data.model.LoginResponse

interface AuthenticationApi {

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}
