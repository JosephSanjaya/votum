/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.registration.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.registration.domain.model.RegistrationFormData
import io.votum.registration.domain.model.RegistrationFormErrors
import io.votum.registration.domain.model.RegistrationResult
import io.votum.registration.presentation.component.RegistrationTextField
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: RegistrationViewModel = koinViewModel()
) {
    val state by viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.registrationResult) {
        when (val result = state.registrationResult) {
            is RegistrationResult.Success -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.onEvent(RegistrationEvent.NavigateToLogin)
            }
            is RegistrationResult.Error -> {
                snackbarHostState.showSnackbar(result.message)
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text(
                            text = "⬅️",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            RegistrationContent(
                state = state,
                onEvent = viewModel::onEvent,
                modifier = Modifier.fillMaxSize()
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Creating your account...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RegistrationContent(
    state: RegistrationState,
    onEvent: (RegistrationEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Join Polkadot Votum",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Create your secure voting account to participate in blockchain-powered elections",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 32.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RegistrationTextField(
                    value = state.formData.nationalId,
                    onValueChange = { onEvent(RegistrationEvent.UpdateNationalId(it)) },
                    label = "National ID",
                    placeholder = "Enter your national ID number",
                    errorMessage = state.formErrors.nationalId,
                    keyboardType = KeyboardType.Number,
                    leadingIcon = {
                        Text(
                            text = "🆔",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )

                RegistrationTextField(
                    value = state.formData.fullName,
                    onValueChange = { onEvent(RegistrationEvent.UpdateFullName(it)) },
                    label = "Full Name",
                    placeholder = "Enter your full legal name",
                    errorMessage = state.formErrors.fullName,
                    leadingIcon = {
                        Text(
                            text = "👤",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )

                RegistrationTextField(
                    value = state.formData.email,
                    onValueChange = { onEvent(RegistrationEvent.UpdateEmail(it)) },
                    label = "Email Address",
                    placeholder = "Enter your email address",
                    errorMessage = state.formErrors.email,
                    keyboardType = KeyboardType.Email,
                    leadingIcon = {
                        Text(
                            text = "📧",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )

                RegistrationTextField(
                    value = state.formData.phoneNumber,
                    onValueChange = { onEvent(RegistrationEvent.UpdatePhoneNumber(it)) },
                    label = "Phone Number",
                    placeholder = "Enter your phone number",
                    errorMessage = state.formErrors.phoneNumber,
                    keyboardType = KeyboardType.Phone,
                    leadingIcon = {
                        Text(
                            text = "📱",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )

                RegistrationTextField(
                    value = state.formData.dateOfBirth,
                    onValueChange = { onEvent(RegistrationEvent.UpdateDateOfBirth(it)) },
                    label = "Date of Birth",
                    placeholder = "YYYY-MM-DD",
                    errorMessage = state.formErrors.dateOfBirth,
                    keyboardType = KeyboardType.Number,
                    leadingIcon = {
                        Text(
                            text = "📅",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )

                RegistrationTextField(
                    value = state.formData.address,
                    onValueChange = { onEvent(RegistrationEvent.UpdateAddress(it)) },
                    label = "Address",
                    placeholder = "Enter your full address",
                    errorMessage = state.formErrors.address,
                    singleLine = false,
                    maxLines = 3,
                    imeAction = ImeAction.Next,
                    leadingIcon = {
                        Text(
                            text = "🏠",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )

                RegistrationTextField(
                    value = state.formData.password,
                    onValueChange = { onEvent(RegistrationEvent.UpdatePassword(it)) },
                    label = "Password",
                    placeholder = "Create a strong password",
                    errorMessage = state.formErrors.password,
                    isPassword = true
                )

                RegistrationTextField(
                    value = state.formData.confirmPassword,
                    onValueChange = { onEvent(RegistrationEvent.UpdateConfirmPassword(it)) },
                    label = "Confirm Password",
                    placeholder = "Confirm your password",
                    errorMessage = state.formErrors.confirmPassword,
                    isPassword = true,
                    imeAction = ImeAction.Done
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = state.formData.acceptedTerms,
                    onCheckedChange = { onEvent(RegistrationEvent.UpdateAcceptedTerms(it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "I agree to the Terms of Service and Privacy Policy",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (state.formErrors.acceptedTerms != null) {
                        Text(
                            text = state.formErrors.acceptedTerms,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onEvent(RegistrationEvent.ValidateForm)
                onEvent(RegistrationEvent.SubmitRegistration)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !state.isLoading
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = { onEvent(RegistrationEvent.NavigateToLogin) }
            ) {
                Text(
                    text = "Sign In",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
private fun RegistrationScreenPreview() {
    VotumTheme {
        RegistrationContent(
            state = RegistrationState(
                formData = RegistrationFormData(
                    nationalId = "1234567890",
                    fullName = "John Doe",
                    email = "john.doe@example.com",
                    phoneNumber = "+1234567890",
                    dateOfBirth = "1990-01-01",
                    address = "123 Main Street, City, State",
                    password = "password123",
                    confirmPassword = "password123",
                    acceptedTerms = true
                ),
                formErrors = RegistrationFormErrors(
                    password = "Password must contain at least one uppercase letter"
                )
            ),
            onEvent = {}
        )
    }
}
