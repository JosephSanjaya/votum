/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.presentation.screen.model

data class IdentityVerificationScreenState(
    val nationalId: String = "",
    val verificationCode: String = "",
    val documentProof: String? = null,
    val documentFileName: String? = null,
    val documentFileSize: Long = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val nationalIdError: String? = null,
    val verificationCodeError: String? = null,
    val documentError: String? = null,
    val showCameraCapture: Boolean = false
)
