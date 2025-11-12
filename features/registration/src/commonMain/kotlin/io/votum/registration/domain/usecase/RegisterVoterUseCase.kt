/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.domain.usecase

import io.votum.registration.data.model.RegistrationRequest
import io.votum.registration.data.model.RegistrationResponse
import io.votum.registration.domain.model.RegistrationFormData
import io.votum.registration.domain.model.RegistrationFormErrors
import io.votum.registration.domain.model.RegistrationResult
import io.votum.registration.domain.repository.RegistrationRepository
import io.votum.registration.domain.validator.RegistrationValidator
import org.koin.core.annotation.Single

@Single
class RegisterVoterUseCase(
    private val registrationRepository: RegistrationRepository,
    private val registrationValidator: RegistrationValidator
) {

    suspend fun execute(formData: RegistrationFormData): RegistrationResult {
        val validationErrors = registrationValidator.validateRegistrationForm(formData)

        if (validationErrors.hasErrors) {
            return RegistrationResult.Error(
                message = "Please fix the errors below",
                errors = validationErrors
            )
        }

        return try {
            val registrationRequest = mapFormDataToRequest(formData)
            val response = registrationRepository.registerVoter(registrationRequest)

            if (response.success) {
                RegistrationResult.Success(
                    message = response.message
                )
            } else {
                val formErrors = mapApiErrorsToFormErrors(response)
                RegistrationResult.Error(
                    message = response.message,
                    errors = formErrors
                )
            }
        } catch (exception: Exception) {
            RegistrationResult.Error(
                message = "Registration failed. Please check your connection and try again."
            )
        }
    }

    private fun mapFormDataToRequest(formData: RegistrationFormData): RegistrationRequest {
        return RegistrationRequest(
            nationalId = formData.nationalId.trim(),
            email = formData.email.trim().lowercase(),
            password = formData.password,
            fullName = formData.fullName.trim(),
            dateOfBirth = formData.dateOfBirth,
            address = formData.address.trim(),
            phoneNumber = formData.phoneNumber.trim()
        )
    }

    private fun mapApiErrorsToFormErrors(response: RegistrationResponse): RegistrationFormErrors? {
        if (response.errors.isNullOrEmpty()) return null

        val errorMap = response.errors.associateBy { it.field }

        return RegistrationFormErrors(
            nationalId = errorMap["nationalId"]?.message,
            email = errorMap["email"]?.message,
            password = errorMap["password"]?.message,
            fullName = errorMap["fullName"]?.message,
            dateOfBirth = errorMap["dateOfBirth"]?.message,
            address = errorMap["address"]?.message,
            phoneNumber = errorMap["phoneNumber"]?.message
        )
    }
}
