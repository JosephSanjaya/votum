/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.presentation.screen

import io.votum.core.presentation.navigation.NavigationEvent
import io.votum.core.presentation.utils.BaseViewModel
import io.votum.registration.domain.model.RegistrationFormData
import io.votum.registration.domain.model.RegistrationFormErrors
import io.votum.registration.domain.model.RegistrationResult
import io.votum.registration.domain.usecase.RegisterVoterUseCase
import io.votum.registration.domain.validator.RegistrationValidator
import org.koin.core.annotation.Factory

data class RegistrationState(
    val formData: RegistrationFormData = RegistrationFormData(),
    val formErrors: RegistrationFormErrors = RegistrationFormErrors(),
    val registrationResult: RegistrationResult = RegistrationResult.Idle,
    val isLoading: Boolean = false
)

sealed class RegistrationEvent {
    data class UpdateNationalId(val nationalId: String) : RegistrationEvent()
    data class UpdateEmail(val email: String) : RegistrationEvent()
    data class UpdatePassword(val password: String) : RegistrationEvent()
    data class UpdateConfirmPassword(val confirmPassword: String) : RegistrationEvent()
    data class UpdateFullName(val fullName: String) : RegistrationEvent()
    data class UpdateDateOfBirth(val dateOfBirth: String) : RegistrationEvent()
    data class UpdateAddress(val address: String) : RegistrationEvent()
    data class UpdatePhoneNumber(val phoneNumber: String) : RegistrationEvent()
    data class UpdateAcceptedTerms(val accepted: Boolean) : RegistrationEvent()
    data object ValidateForm : RegistrationEvent()
    data object SubmitRegistration : RegistrationEvent()
    data object ClearErrors : RegistrationEvent()
    data object NavigateToLogin : RegistrationEvent()
}

@Factory
class RegistrationViewModel(
    private val registerVoterUseCase: RegisterVoterUseCase,
    private val registrationValidator: RegistrationValidator
) : BaseViewModel<RegistrationState, RegistrationEvent>(
    initialState = RegistrationState()
) {

    override fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.UpdateNationalId -> updateNationalId(event.nationalId)
            is RegistrationEvent.UpdateEmail -> updateEmail(event.email)
            is RegistrationEvent.UpdatePassword -> updatePassword(event.password)
            is RegistrationEvent.UpdateConfirmPassword -> updateConfirmPassword(event.confirmPassword)
            is RegistrationEvent.UpdateFullName -> updateFullName(event.fullName)
            is RegistrationEvent.UpdateDateOfBirth -> updateDateOfBirth(event.dateOfBirth)
            is RegistrationEvent.UpdateAddress -> updateAddress(event.address)
            is RegistrationEvent.UpdatePhoneNumber -> updatePhoneNumber(event.phoneNumber)
            is RegistrationEvent.UpdateAcceptedTerms -> updateAcceptedTerms(event.accepted)
            is RegistrationEvent.ValidateForm -> validateForm()
            is RegistrationEvent.SubmitRegistration -> submitRegistration()
            is RegistrationEvent.ClearErrors -> clearErrors()
            is RegistrationEvent.NavigateToLogin -> navigateToLogin()
        }
    }

    private fun updateNationalId(nationalId: String) = intent {
        reduce {
            state.copy(
                formData = state.formData.copy(nationalId = nationalId),
                formErrors = state.formErrors.copy(nationalId = null)
            )
        }
    }

    private fun updateEmail(email: String) = intent {
        reduce {
            state.copy(
                formData = state.formData.copy(email = email),
                formErrors = state.formErrors.copy(email = null)
            )
        }
    }

    private fun updatePassword(password: String) = intent {
        reduce {
            state.copy(
                formData = state.formData.copy(password = password),
                formErrors = state.formErrors.copy(password = null)
            )
        }
    }

    private fun updateConfirmPassword(confirmPassword: String) = intent {
        reduce {
            state.copy(
                formData = state.formData.copy(confirmPassword = confirmPassword),
                formErrors = state.formErrors.copy(confirmPassword = null)
            )
        }
    }

    private fun updateFullName(fullName: String) = intent {
        reduce {
            state.copy(
                formData = state.formData.copy(fullName = fullName),
                formErrors = state.formErrors.copy(fullName = null)
            )
        }
    }

    private fun updateDateOfBirth(dateOfBirth: String) = intent {
        reduce {
            state.copy(
                formData = state.formData.copy(dateOfBirth = dateOfBirth),
                formErrors = state.formErrors.copy(dateOfBirth = null)
            )
        }
    }

    private fun updateAddress(address: String) = intent {
        reduce {
            state.copy(
                formData = state.formData.copy(address = address),
                formErrors = state.formErrors.copy(address = null)
            )
        }
    }

    private fun updatePhoneNumber(phoneNumber: String) = intent {
        reduce {
            state.copy(
                formData = state.formData.copy(phoneNumber = phoneNumber),
                formErrors = state.formErrors.copy(phoneNumber = null)
            )
        }
    }

    private fun updateAcceptedTerms(accepted: Boolean) = intent {
        reduce {
            state.copy(
                formData = state.formData.copy(acceptedTerms = accepted),
                formErrors = state.formErrors.copy(acceptedTerms = null)
            )
        }
    }

    private fun validateForm() = intent {
        val validationErrors = registrationValidator.validateRegistrationForm(state.formData)
        reduce {
            state.copy(formErrors = validationErrors)
        }
    }

    private fun submitRegistration() = intent {
        reduce { state.copy(isLoading = true, registrationResult = RegistrationResult.Loading) }

        val result = safeIoCall {
            registerVoterUseCase.execute(state.formData)
        }

        reduce {
            state.copy(
                isLoading = false,
                registrationResult = result ?: RegistrationResult.Error("Registration failed. Please try again."),
                formErrors = if (result is RegistrationResult.Error && result.errors != null) {
                    result.errors
                } else {
                    RegistrationFormErrors()
                }
            )
        }
    }

    private fun clearErrors() = intent {
        reduce {
            state.copy(
                formErrors = RegistrationFormErrors(),
                registrationResult = RegistrationResult.Idle
            )
        }
    }

    private fun navigateToLogin() = intent {
        navigationEventBus.post(RegistrationNavigationEvent.NavigateToLogin)
    }
}

sealed class RegistrationNavigationEvent {
    data object NavigateToLogin : RegistrationNavigationEvent(), NavigationEvent
}
