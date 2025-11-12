/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.domain.repository

import io.votum.registration.data.model.RegistrationRequest
import io.votum.registration.data.model.RegistrationResponse

interface RegistrationRepository {
    suspend fun registerVoter(request: RegistrationRequest): RegistrationResponse
}
