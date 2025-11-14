/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.domain.model

data class VerificationData(
    val nationalId: String,
    val verificationCode: String,
    val documentProof: String
)
