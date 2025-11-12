/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationResponse(
    val success: Boolean,
    val data: VoterData? = null,
    val message: String,
    val timestamp: String,
    val requestId: String,
    val errors: List<ValidationError>? = null
)

@Serializable
data class VoterData(
    val id: String,
    val nationalId: String,
    val email: String,
    val fullName: String,
    val isVerified: Boolean,
    val isEligible: Boolean,
    val publicKey: String,
    val registrationDate: String,
    val lastLoginDate: String?
)

@Serializable
data class ValidationError(
    val field: String,
    val message: String,
    val code: String
)
