package io.votum.registration.presentation.screen.model

import io.votum.registration.domain.model.RegistrationFormData
import io.votum.registration.domain.model.RegistrationFormErrors

data class RegistrationScreenState(
    val formData: RegistrationFormData = RegistrationFormData(),
    val formErrors: RegistrationFormErrors = RegistrationFormErrors(),
    val isLoading: Boolean = false
)
