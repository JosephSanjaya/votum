/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.domain.model

data class RegistrationFormData(
    val nationalId: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "",
    val dateOfBirth: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val acceptedTerms: Boolean = false
)

data class RegistrationFormErrors(
    val nationalId: String? = null,
    val email: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val fullName: String? = null,
    val dateOfBirth: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val acceptedTerms: String? = null
) {
    val hasErrors: Boolean
        get() = nationalId != null || email != null || password != null ||
            confirmPassword != null || fullName != null || dateOfBirth != null ||
            address != null || phoneNumber != null || acceptedTerms != null
}

sealed class RegistrationResult {
    data object Loading : RegistrationResult()
    data class Success(val message: String) : RegistrationResult()
    data class Error(val message: String, val errors: RegistrationFormErrors? = null) : RegistrationResult()
    data object Idle : RegistrationResult()
}
