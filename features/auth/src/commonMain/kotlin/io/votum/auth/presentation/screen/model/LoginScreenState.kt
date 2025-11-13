/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.auth.presentation.screen.model

data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)
