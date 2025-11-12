/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.domain.validator

import io.votum.registration.domain.model.RegistrationFormData
import io.votum.registration.domain.model.RegistrationFormErrors
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.core.annotation.Single
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Single
class RegistrationValidator {

    fun validateRegistrationForm(formData: RegistrationFormData): RegistrationFormErrors {
        return RegistrationFormErrors(
            nationalId = validateNationalId(formData.nationalId),
            email = validateEmail(formData.email),
            password = validatePassword(formData.password),
            confirmPassword = validateConfirmPassword(formData.password, formData.confirmPassword),
            fullName = validateFullName(formData.fullName),
            dateOfBirth = validateDateOfBirth(formData.dateOfBirth),
            address = validateAddress(formData.address),
            phoneNumber = validatePhoneNumber(formData.phoneNumber),
            acceptedTerms = validateAcceptedTerms(formData.acceptedTerms)
        )
    }

    private fun validateNationalId(nationalId: String): String? {
        return when {
            nationalId.isBlank() -> "National ID is required"
            nationalId.length < 10 -> "National ID must be at least 10 characters"
            nationalId.length > 20 -> "National ID must be less than 20 characters"
            !nationalId.all { it.isDigit() } -> "National ID must contain only numbers"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
        return when {
            email.isBlank() -> "Email is required"
            !email.matches(emailRegex) -> "Please enter a valid email address"
            email.length > 254 -> "Email address is too long"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            password.length > 128 -> "Password must be less than 128 characters"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any {
                "!@#$%^&*()_+-=[]{}|;:,.<>?".contains(
                    it
                )
            } -> "Password must contain at least one special character"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }

    private fun validateFullName(fullName: String): String? {
        return when {
            fullName.isBlank() -> "Full name is required"
            fullName.trim().length < 2 -> "Full name must be at least 2 characters"
            fullName.length > 100 -> "Full name must be less than 100 characters"
            !fullName.all { it.isLetter() || it.isWhitespace() || it == '\'' || it == '-' } ->
                "Full name can only contain letters, spaces, apostrophes, and hyphens"
            else -> null
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun validateDateOfBirth(dateOfBirth: String): String? {
        return when {
            dateOfBirth.isBlank() -> "Date of birth is required"
            else -> {
                try {
                    val birthDate = LocalDate.parse(dateOfBirth)
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    val age = today.year - birthDate.year -
                        if (today.month < birthDate.month ||
                            (today.month == birthDate.month && today.day < birthDate.day)
                        ) {
                            1
                        } else {
                            0
                        }

                    when {
                        age < 18 -> "You must be at least 18 years old to register"
                        age > 120 -> "Please enter a valid date of birth"
                        birthDate > today -> "Date of birth cannot be in the future"
                        else -> null
                    }
                } catch (_: Exception) {
                    "Please enter a valid date (YYYY-MM-DD)"
                }
            }
        }
    }

    private fun validateAddress(address: String): String? {
        return when {
            address.isBlank() -> "Address is required"
            address.trim().length < 10 -> "Address must be at least 10 characters"
            address.length > 500 -> "Address must be less than 500 characters"
            else -> null
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): String? {
        val phoneRegex = "^\\+?[1-9]\\d{1,14}$".toRegex()
        return when {
            phoneNumber.isBlank() -> "Phone number is required"
            !phoneNumber.matches(phoneRegex) -> "Please enter a valid phone number"
            else -> null
        }
    }

    private fun validateAcceptedTerms(acceptedTerms: Boolean): String? {
        return if (!acceptedTerms) {
            "You must accept the terms and conditions to register"
        } else {
            null
        }
    }
}
