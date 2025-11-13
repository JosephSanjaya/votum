/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.presentation.screen

import io.votum.core.presentation.utils.BaseViewModel
import io.votum.core.presentation.utils.VotumDispatchers
import io.votum.registration.domain.model.RegistrationFormErrors
import io.votum.registration.domain.model.RegistrationResult
import io.votum.registration.domain.usecase.RegisterVoterUseCase
import io.votum.registration.domain.validator.RegistrationValidator
import io.votum.registration.presentation.screen.model.RegistrationScreenIntent
import io.votum.registration.presentation.screen.model.RegistrationScreenState
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class RegistrationViewModel(
    private val registerVoterUseCase: RegisterVoterUseCase,
    private val registrationValidator: RegistrationValidator,
    private val dispatchers: VotumDispatchers
) : BaseViewModel<RegistrationScreenState, Unit>(
    initialState = RegistrationScreenState()
) {

    override fun onIntent(intent: Any) {
        when (intent) {
            is RegistrationScreenIntent.UpdateNationalId -> updateNationalId(intent.nationalId)
            is RegistrationScreenIntent.UpdateEmail -> updateEmail(intent.email)
            is RegistrationScreenIntent.UpdatePassword -> updatePassword(intent.password)
            is RegistrationScreenIntent.UpdateConfirmPassword -> updateConfirmPassword(intent.confirmPassword)
            is RegistrationScreenIntent.UpdateFullName -> updateFullName(intent.fullName)
            is RegistrationScreenIntent.UpdateDateOfBirth -> updateDateOfBirth(intent.dateOfBirth)
            is RegistrationScreenIntent.UpdateAddress -> updateAddress(intent.address)
            is RegistrationScreenIntent.UpdatePhoneNumber -> updatePhoneNumber(intent.phoneNumber)
            is RegistrationScreenIntent.UpdateAcceptedTerms -> updateAcceptedTerms(intent.accepted)
            is RegistrationScreenIntent.ValidateForm -> validateForm()
            is RegistrationScreenIntent.SubmitRegistration -> submitRegistration()
            is RegistrationScreenIntent.ClearErrors -> clearErrors()
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
        reduce { state.copy(isLoading = true) }

        val result = withContext(dispatchers.io) {
            registerVoterUseCase.runCatching {
                execute(state.formData)
            }.getOrNull()?.takeIf {
                it is RegistrationResult.Success
            }
        }
        sendIntent(
            if (result == null) {
                RegistrationScreenIntent.OnRegisterFailed(
                    "Registration failed. Please try again."
                )
            } else {
                RegistrationScreenIntent.OnRegisterSuccessful
            }
        )

        reduce {
            state.copy(
                isLoading = false
            )
        }
    }

    private fun clearErrors() = intent {
        reduce {
            state.copy(
                formErrors = RegistrationFormErrors()
            )
        }
    }
}
