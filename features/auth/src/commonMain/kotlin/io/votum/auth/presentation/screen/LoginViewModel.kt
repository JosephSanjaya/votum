/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.auth.presentation.screen

import io.votum.auth.data.model.LoginRequest
import io.votum.auth.domain.usecase.LoginUseCase
import io.votum.auth.presentation.screen.model.LoginScreenIntent
import io.votum.auth.presentation.screen.model.LoginScreenState
import io.votum.core.presentation.utils.BaseViewModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginScreenState, Unit>(
    initialState = LoginScreenState()
) {

    override fun onIntent(intent: Any) {
        when (intent) {
            is LoginScreenIntent.Login -> login(intent.email, intent.password)
            is LoginScreenIntent.EmailChanged -> onEmailChanged(intent.email)
            is LoginScreenIntent.PasswordChanged -> onPasswordChanged(intent.password)
        }
    }

    private fun onEmailChanged(email: String) = intent {
        reduce {
            state.copy(email = email)
        }
    }

    private fun onPasswordChanged(password: String) = intent {
        reduce {
            state.copy(password = password)
        }
    }

    private fun login(email: String, password: String) = intent {
        reduce {
            state.copy(isLoading = true)
        }
        loginUseCase(LoginRequest(email, password))
            .onSuccess {
                reduce {
                    state.copy(isLoading = false)
                }
                sendIntent(LoginScreenIntent.NavigateToElectionList)
            }
            .onFailure {
                reduce {
                    state.copy(isLoading = false)
                }
                sendIntent(LoginScreenIntent.LoginFailed(it.message ?: "An unknown error occurred"))
            }
    }
}
