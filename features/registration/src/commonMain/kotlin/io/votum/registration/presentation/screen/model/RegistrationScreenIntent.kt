package io.votum.registration.presentation.screen.model

import androidx.compose.material3.SnackbarVisuals
import io.votum.core.presentation.component.DefaultSnackBarVisuals
import io.votum.core.presentation.navigation.NavigationIntent

sealed interface RegistrationScreenIntent {
    data class UpdateNationalId(val nationalId: String) : RegistrationScreenIntent
    data class UpdateEmail(val email: String) : RegistrationScreenIntent
    data class UpdatePassword(val password: String) : RegistrationScreenIntent
    data class UpdateConfirmPassword(val confirmPassword: String) : RegistrationScreenIntent
    data class UpdateFullName(val fullName: String) : RegistrationScreenIntent
    data class UpdateDateOfBirth(val dateOfBirth: String) : RegistrationScreenIntent
    data class UpdateAddress(val address: String) : RegistrationScreenIntent
    data class UpdatePhoneNumber(val phoneNumber: String) : RegistrationScreenIntent
    data class UpdateAcceptedTerms(val accepted: Boolean) : RegistrationScreenIntent
    data object ValidateForm : RegistrationScreenIntent
    data object SubmitRegistration : RegistrationScreenIntent
    data object ClearErrors : RegistrationScreenIntent
    data object NavigateToLogin : RegistrationScreenIntent, NavigationIntent

    data object OnRegisterSuccessful :
        RegistrationScreenIntent,
        SnackbarVisuals by DefaultSnackBarVisuals(
            "Registration successful"
        )

    data class OnRegisterFailed(override val message: String) :
        RegistrationScreenIntent,
        SnackbarVisuals by DefaultSnackBarVisuals(message)
}
