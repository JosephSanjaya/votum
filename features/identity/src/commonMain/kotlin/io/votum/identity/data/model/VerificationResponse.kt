/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VerificationResponse(
    val success: Boolean,
    val data: VoterData?,
    val message: String,
    val timestamp: String,
    val requestId: String,
    val errors: List<ApiError>? = null
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
data class ApiError(
    val field: String,
    val message: String,
    val code: String
)
