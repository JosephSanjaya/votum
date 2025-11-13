/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.auth.data.repository

import de.jensklingenberg.ktorfit.Ktorfit
import io.votum.auth.data.remote.AuthenticationApi
import io.votum.auth.data.remote.createAuthenticationApi
import io.votum.auth.domain.repository.AuthenticationRepository
import org.koin.core.annotation.Single

@Single
class AuthenticationRepositoryImpl(
    ktorfit: Ktorfit
) : AuthenticationRepository, AuthenticationApi by ktorfit.createAuthenticationApi()
