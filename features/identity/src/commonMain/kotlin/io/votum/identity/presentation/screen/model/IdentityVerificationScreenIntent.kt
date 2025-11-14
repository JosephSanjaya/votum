/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.presentation.screen.model

import io.votum.core.presentation.navigation.NavigationIntent

sealed interface IdentityVerificationScreenIntent {
    data class UpdateNationalId(val nationalId: String) : IdentityVerificationScreenIntent
    data class UpdateVerificationCode(val code: String) : IdentityVerificationScreenIntent
    data class DocumentSelected(
        val documentProof: String,
        val fileName: String,
        val fileSize: Long
    ) : IdentityVerificationScreenIntent
    data object RemoveDocument : IdentityVerificationScreenIntent
    data object OpenCameraCapture : IdentityVerificationScreenIntent
    data object CloseCameraCapture : IdentityVerificationScreenIntent
    data class CameraPhotoTaken(val photoBase64: String) : IdentityVerificationScreenIntent
    data object SubmitVerification : IdentityVerificationScreenIntent
    data object RetryVerification : IdentityVerificationScreenIntent
    data object DismissError : IdentityVerificationScreenIntent
    data object NavigateToLogin : IdentityVerificationScreenIntent, NavigationIntent
}
