/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.app.presentation.navigation.handler

import io.votum.registration.presentation.screen.RegistrationViewModel
import org.koin.core.annotation.Factory

@Factory
class RegistrationStateProvider(
    private val registrationViewModel: RegistrationViewModel
) {
    fun getNationalId(): String? {
        return registrationViewModel.container.stateFlow.value.formData.nationalId.takeIf { it.isNotBlank() }
    }
}
