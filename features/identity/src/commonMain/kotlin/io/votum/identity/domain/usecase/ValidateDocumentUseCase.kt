/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.domain.usecase

import io.votum.identity.domain.model.DocumentValidationResult
import org.koin.core.annotation.Single

@Single
class ValidateDocumentUseCase {
    operator fun invoke(documentProof: String): DocumentValidationResult {
        val decodedSize = calculateBase64Size(documentProof)
        val maxSize = 5 * 1024 * 1024

        if (decodedSize > maxSize) {
            return DocumentValidationResult(
                isValid = false,
                errorMessage = "File size must be less than 5MB"
            )
        }

        if (!isValidBase64(documentProof)) {
            return DocumentValidationResult(
                isValid = false,
                errorMessage = "Invalid document format"
            )
        }

        return DocumentValidationResult(isValid = true)
    }

    private fun calculateBase64Size(base64: String): Long {
        val cleanBase64 = base64.substringAfter("base64,", base64)
        val padding = cleanBase64.count { it == '=' }
        return ((cleanBase64.length * 3L) / 4L) - padding
    }

    private fun isValidBase64(base64: String): Boolean {
        return base64.contains("data:image/") && base64.contains("base64,")
    }
}
