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
            is IdentityVerificationScreenIntent.RetryVerification -> retryVerification()
            is IdentityVerificationScreenIntent.DismissError -> dismissError()
        }
    }

    private fun updateNationalId(nationalId: String) = intent {
        val error = validateNationalIdFormat(nationalId)
        reduce {
            state.copy(
                nationalId = nationalId,
                nationalIdError = error
            )
        }
    }

    private fun updateVerificationCode(code: String) = intent {
        val error = validateVerificationCodeFormat(code)
        reduce {
            state.copy(
                verificationCode = code,
                verificationCodeError = error
            )
        }
    }

    private fun handleDocumentSelected(
        documentProof: String,
        fileName: String,
        fileSize: Long
    ) = intent {
        val error = validateDocumentFormat(documentProof, fileName, fileSize)
        reduce {
            state.copy(
                documentProof = documentProof,
                documentFileName = fileName,
                documentFileSize = fileSize,
                documentError = error
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
        } else {
            val formatError = validateNationalIdFormat(currentState.nationalId)
            if (formatError != null) {
                nationalIdError = formatError
                isValid = false
            }
        }

        if (currentState.verificationCode.isBlank()) {
            verificationCodeError = "Verification code is required"
            isValid = false
        } else {
            val formatError = validateVerificationCodeFormat(currentState.verificationCode)
            if (formatError != null) {
                verificationCodeError = formatError
                isValid = false
            }
        }

        if (currentState.documentProof == null) {
            documentError = "Document proof is required"
            isValid = false
        } else if (currentState.documentFileName != null) {
            val formatError = validateDocumentFormat(
                currentState.documentProof,
                currentState.documentFileName,
                currentState.documentFileSize
            )
            if (formatError != null) {
                documentError = formatError
                isValid = false
            }
        }

        if (!isValid) {
            reduce {
                state.copy(
                    nationalIdError = nationalIdError,
                    verificationCodeError = verificationCodeError,
                    documentError = documentError
                )
            }
            sendIntent(
                DefaultSnackBarVisuals(
                    message = "Please fix the errors before submitting",
                    duration = SnackbarDuration.Short
                )
            )
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
            reduce {
                state.copy(
                    isLoading = false,
                    retryCount = 0,
                    isNetworkError = false,
                    canRetry = false
                )
            }
            if (response.success) {
                sendIntent(
                    DefaultSnackBarVisuals(
                        message = "Identity verified successfully! You can now log in.",
                        duration = SnackbarDuration.Short
                    )
                )
                navigateToLogin()
            } else {
                reduce {
                    state.copy(
                        errorMessage = response.message,
                        canRetry = true
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
            val isNetworkError = isNetworkException(error)
            val errorMessage = when {
                isNetworkError -> "Network error. Please check your connection and try again."
                error.message?.contains("timeout", ignoreCase = true) == true ->
                    "Request timed out. Please try again."
                else -> error.message ?: "Verification failed. Please try again."
            }

            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = errorMessage,
                    isNetworkError = isNetworkError,
                    canRetry = true,
                    retryCount = state.retryCount + 1
                )
            }
            sendIntent(
                DefaultSnackBarVisuals(
                    message = errorMessage,
                    duration = SnackbarDuration.Long
                )
            )
        }
    }

    private fun retryVerification() = intent {
        reduce {
            state.copy(
                errorMessage = null,
                isNetworkError = false,
                canRetry = false
            )
        }
        submitVerification()
    }

    private fun dismissError() = intent {
        reduce {
            state.copy(
                errorMessage = null,
                isNetworkError = false,
                canRetry = false
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

    private fun validateNationalIdFormat(nationalId: String): String? {
        return when {
            nationalId.isBlank() -> null
            nationalId.length < 10 -> "National ID must be at least 10 characters"
            nationalId.length > 20 -> "National ID must not exceed 20 characters"
            !nationalId.matches(Regex("^[A-Za-z0-9]+$")) -> "National ID must contain only letters and numbers"
            else -> null
        }
    }

    private fun validateVerificationCodeFormat(code: String): String? {
        return when {
            code.isBlank() -> null
            code.length < 6 -> "Verification code must be at least 6 characters"
            code.length > 20 -> "Verification code must not exceed 20 characters"
            !code.matches(Regex("^[A-Za-z0-9]+$")) -> "Verification code must be alphanumeric"
            else -> null
        }
    }

    private fun validateDocumentFormat(documentProof: String, fileName: String, fileSize: Long): String? {
        val maxSize = 5 * 1024 * 1024

        if (fileSize > maxSize) {
            return "File size must be less than 5MB"
        }

        val allowedExtensions = listOf("jpg", "jpeg", "png", "pdf")
        val extension = fileName.substringAfterLast(".", "").lowercase()

        if (extension !in allowedExtensions) {
            return "Only JPEG, PNG, and PDF files are allowed"
        }

        if (!documentProof.contains("data:image/") && !documentProof.contains("data:application/pdf")) {
            return "Invalid document format"
        }

        return null
    }

    private fun isNetworkException(error: Throwable): Boolean {
        val errorMessage = error.message?.lowercase() ?: ""
        return errorMessage.contains("network") ||
            errorMessage.contains("connection") ||
            errorMessage.contains("unreachable") ||
            errorMessage.contains("no internet") ||
            error::class.simpleName?.contains("IOException") == true ||
            error::class.simpleName?.contains("SocketException") == true ||
            error::class.simpleName?.contains("UnknownHostException") == true
    }
}
