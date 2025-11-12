/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.data.repository

import io.votum.registration.data.model.RegistrationRequest
import io.votum.registration.data.model.RegistrationResponse
import io.votum.registration.data.remote.RegistrationApi
import io.votum.registration.domain.repository.RegistrationRepository
import de.jensklingenberg.ktorfit.Ktorfit
import io.votum.registration.data.remote.createRegistrationApi
import org.koin.core.annotation.Single

@Single
class RegistrationRepositoryImpl(
    ktorfit: Ktorfit
) : RegistrationRepository, RegistrationApi by ktorfit.createRegistrationApi() {

    override suspend fun registerVoter(request: RegistrationRequest): RegistrationResponse {
        return registerVoter(request)
    }
}
