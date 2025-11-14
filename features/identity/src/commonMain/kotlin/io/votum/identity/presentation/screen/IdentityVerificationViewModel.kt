/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.presentation.screen

import androidx.compose.material3.SnackbarDuration
import io.votum.core.presentation.component.DefaultSnackBarVisuals
import io.votum.core.presentation.utils.BaseViewModel
import io.votum.core.presentation.utils.VotumDispatchers
import io.votum.identity.domain.model.VerificationData
import io.votum.identity.domain.usecase.VerifyIdentityUseCase
import io.votum.identity.presentation.screen.model.IdentityVerificationScreenIntent
import io.votum.identity.presentation.screen.model.IdentityVerificationScreenState
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class IdentityVerificationViewModel(
    private val verifyIdentityUseCase: VerifyIdentityUseCase,
    private val dispatchers: VotumDispatchers
) : BaseViewModel<IdentityVerificationScreenState, Unit>(
    initialState = IdentityVerificationScreenState()
) {

    override fun onIntent(intent: Any) {
        when (intent) {
            is IdentityVerificationScreenIntent.UpdateNationalId -> updateNationalId(intent.nationalId)
            is IdentityVerificationScreenIntent.UpdateVerificationCode -> updateVerificationCode(intent.code)
            is IdentityVerificationScreenIntent.DocumentSelected -> handleDocumentSelected(
                intent.documentProof,
                intent.fileName,
                intent.fileSize
            )
            is IdentityVerificationScreenIntent.RemoveDocument -> removeDocument()
            is IdentityVerificationScreenIntent.OpenCameraCapture -> openCameraCapture()
            is IdentityVerificationScreenIntent.CloseCameraCapture -> closeCameraCapture()
            is IdentityVerificationScreenIntent.CameraPhotoTaken -> handleCameraPhoto(intent.photoBase64)
            is IdentityVerificationScreenIntent.SubmitVerification -> submitVerification()
            is IdentityVerificationScreenIntent.NavigateToLogin -> navigateToLogin()
        }
    }

    private fun updateNationalId(nationalId: String) = intent {
        reduce {
            state.copy(
                nationalId = nationalId,
                nationalIdError = null
            )
        }
    }

    private fun updateVerificationCode(code: String) = intent {
        reduce {
            state.copy(
                verificationCode = code,
                verificationCodeError = null
            )
        }
    }

    private fun handleDocumentSelected(
        documentProof: String,
        fileName: String,
        fileSize: Long
    ) = intent {
        reduce {
            state.copy(
                documentProof = documentProof,
                documentFileName = fileName,
                documentFileSize = fileSize,
                documentError = null
            )
        }
    }

    private fun removeDocument() = intent {
        reduce {
            state.copy(
                documentProof = null,
                documentFileName = null,
                documentFileSize = 0,
                documentError = null
            )
        }
    }

    private fun openCameraCapture() = intent {
        reduce {
            state.copy(showCameraCapture = true)
        }
    }

    private fun closeCameraCapture() = intent {
        reduce {
            state.copy(showCameraCapture = false)
        }
    }

    private fun handleCameraPhoto(photoBase64: String) = intent {
        val fileSize = calculateBase64Size(photoBase64)
        reduce {
            state.copy(
                documentProof = photoBase64,
                documentFileName = "camera_capture.jpg",
                documentFileSize = fileSize,
                showCameraCapture = false,
                documentError = null
            )
        }
    }

    private fun submitVerification() = intent {
        val currentState = state
        var isValid = true
        var nationalIdError: String? = null
        var verificationCodeError: String? = null
        var documentError: String? = null

        if (currentState.nationalId.isBlank()) {
            nationalIdError = "National ID is required"
            isValid = false
        } else if (currentState.nationalId.length < 10) {
            nationalIdError = "National ID must be at least 10 characters"
            isValid = false
        }

        if (currentState.verificationCode.isBlank()) {
            verificationCodeError = "Verification code is required"
            isValid = false
        } else if (currentState.verificationCode.length < 6) {
            verificationCodeError = "Verification code must be at least 6 characters"
            isValid = false
        }

        if (currentState.documentProof == null) {
            documentError = "Document proof is required"
            isValid = false
        }

        if (!isValid) {
            reduce {
                state.copy(
                    nationalIdError = nationalIdError,
                    verificationCodeError = verificationCodeError,
                    documentError = documentError
                )
            }
            return@intent
        }

        reduce { state.copy(isLoading = true, errorMessage = null) }

        val verificationData = VerificationData(
            nationalId = currentState.nationalId,
            verificationCode = currentState.verificationCode,
            documentProof = currentState.documentProof!!
        )

        val result = withContext(dispatchers.io) {
            verifyIdentityUseCase(verificationData)
        }

        result.onSuccess { response ->
            reduce { state.copy(isLoading = false) }
            if (response.success) {
                sendIntent(
                    DefaultSnackBarVisuals(
                        message = "Identity verified successfully! You can now log in.",
                        duration = SnackbarDuration.Short
                    )
                )
                sendIntent(IdentityVerificationScreenIntent.NavigateToLogin)
            } else {
                reduce {
                    state.copy(
                        errorMessage = response.message
                    )
                }
                sendIntent(
                    DefaultSnackBarVisuals(
                        message = response.message,
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }.onFailure { error ->
            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Verification failed"
                )
            }
            sendIntent(
                DefaultSnackBarVisuals(
                    message = error.message ?: "Verification failed. Please try again.",
                    duration = SnackbarDuration.Long
                )
            )
        }
    }

    private fun navigateToLogin() = intent {
        sendIntent(IdentityVerificationScreenIntent.NavigateToLogin)
    }

    private fun calculateBase64Size(base64: String): Long {
        val cleanBase64 = base64.substringAfter("base64,", base64)
        val padding = cleanBase64.count { it == '=' }
        return ((cleanBase64.length * 3L) / 4L) - padding
    }
}
