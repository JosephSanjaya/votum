/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.data.repository

import de.jensklingenberg.ktorfit.Ktorfit
import io.votum.identity.data.model.VerificationRequest
import io.votum.identity.data.model.VerificationResponse
import io.votum.identity.data.remote.IdentityApi
import io.votum.identity.data.remote.createIdentityApi
import io.votum.identity.domain.repository.IdentityRepository
import org.koin.core.annotation.Single

@Single
class IdentityRepositoryImpl(
    ktorfit: Ktorfit
) : IdentityRepository {

    private val api: IdentityApi = ktorfit.createIdentityApi()

    override suspend fun verifyIdentity(request: VerificationRequest): VerificationResponse {
        return api.verifyIdentity(request)
    }
}
