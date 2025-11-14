/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VerificationRequest(
    val nationalId: String,
    val verificationCode: String,
    val documentProof: String
)
