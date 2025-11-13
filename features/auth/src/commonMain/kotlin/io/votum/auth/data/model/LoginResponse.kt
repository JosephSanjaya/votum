/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val success: Boolean,
    val data: AuthData,
    val message: String,
    val timestamp: String,
    val requestId: String
)

@Serializable
data class AuthData(
    val voter: Voter,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Serializable
data class Voter(
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
