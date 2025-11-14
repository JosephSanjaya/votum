/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.domain.usecase

import io.votum.identity.data.model.VerificationRequest
import io.votum.identity.data.model.VerificationResponse
import io.votum.identity.domain.model.VerificationData
import io.votum.identity.domain.repository.IdentityRepository
import org.koin.core.annotation.Factory

@Factory
class VerifyIdentityUseCase(
    private val identityRepository: IdentityRepository,
    private val validateDocumentUseCase: ValidateDocumentUseCase
) {
    suspend operator fun invoke(verificationData: VerificationData): Result<VerificationResponse> {
        return runCatching {
            val validationResult = validateDocumentUseCase(verificationData.documentProof)

            if (!validationResult.isValid) {
                throw IllegalArgumentException(validationResult.errorMessage)
            }

            val request = VerificationRequest(
                nationalId = verificationData.nationalId,
                verificationCode = verificationData.verificationCode,
                documentProof = verificationData.documentProof
            )

            identityRepository.verifyIdentity(request)
        }
    }
}
