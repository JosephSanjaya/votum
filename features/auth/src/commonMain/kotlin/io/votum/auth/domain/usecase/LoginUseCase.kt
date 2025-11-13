/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.auth.domain.usecase

import io.votum.auth.data.model.LoginRequest
import io.votum.auth.data.model.LoginResponse
import io.votum.auth.domain.repository.AuthenticationRepository
import org.koin.core.annotation.Factory

@Factory
class LoginUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(loginRequest: LoginRequest): Result<LoginResponse> {
        return runCatching { authenticationRepository.login(loginRequest) }
    }
}
