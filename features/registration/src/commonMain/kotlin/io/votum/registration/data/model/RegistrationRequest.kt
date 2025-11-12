/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val nationalId: String,
    val email: String,
    val password: String,
    val fullName: String,
    val dateOfBirth: String,
    val address: String,
    val phoneNumber: String
)
