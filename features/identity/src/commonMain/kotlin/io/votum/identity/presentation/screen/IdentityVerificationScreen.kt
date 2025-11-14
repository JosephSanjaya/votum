/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.LocalNavController
import io.votum.core.presentation.theme.LocalSnackBarHost
import io.votum.core.presentation.theme.VotumTheme
import io.votum.identity.presentation.component.CameraCapture
import io.votum.identity.presentation.component.DocumentPreview
import io.votum.identity.presentation.component.DocumentUploadCard
import io.votum.identity.presentation.component.VerificationCodeField
import io.votum.identity.presentation.screen.model.IdentityVerificationScreenIntent
import io.votum.identity.presentation.screen.model.IdentityVerificationScreenState
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun IdentityVerificationScreen(
    modifier: Modifier = Modifier,
    viewModel: IdentityVerificationViewModel = koinViewModel()
) {
    val state by viewModel.collectAsState()

    IdentityVerificationContent(
        state = state,
        onIntent = viewModel::sendIntent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdentityVerificationContent(
    state: IdentityVerificationScreenState,
    onIntent: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current
    val snackbarHostState = LocalSnackBarHost.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Verify Identity",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
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
        bottomBar = {
            Column(
                Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
            ) {
                Button(
                    onClick = {
                        onIntent(IdentityVerificationScreenIntent.SubmitVerification)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "Verify Identity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Complete Your Verification",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Upload your identification document and enter " +
                        "your verification code to complete the registration " +
                        "process",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = state.nationalId,
                    onValueChange = {
                        onIntent(IdentityVerificationScreenIntent.UpdateNationalId(it))
                    },
                    label = { Text("National ID") },
                    placeholder = { Text("Enter your national ID number") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.nationalIdError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    leadingIcon = {
                        Text(
                            text = "🆔",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    supportingText = if (state.nationalIdError != null) {
                        { Text(state.nationalIdError) }
                    } else {
                        null
                    }
                )

                VerificationCodeField(
                    value = state.verificationCode,
                    onValueChange = {
                        onIntent(IdentityVerificationScreenIntent.UpdateVerificationCode(it))
                    },
                    errorMessage = state.verificationCodeError,
                    enabled = !state.isLoading
                )

                if (state.documentProof == null) {
                    DocumentUploadCard(
                        onUploadClick = {
                        },
                        onCameraClick = {
                            onIntent(IdentityVerificationScreenIntent.OpenCameraCapture)
                        },
                        errorMessage = state.documentError,
                        enabled = !state.isLoading
                    )
                } else {
                    DocumentPreview(
                        fileName = state.documentFileName ?: "document",
                        fileSize = state.documentFileSize,
                        onRemove = {
                            onIntent(IdentityVerificationScreenIntent.RemoveDocument)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

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
                                    text = "Verifying your identity...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            if (state.showCameraCapture) {
                CameraCapture(
                    onPhotoTaken = { photoBase64 ->
                        onIntent(IdentityVerificationScreenIntent.CameraPhotoTaken(photoBase64))
                    },
                    onCancel = {
                        onIntent(IdentityVerificationScreenIntent.CloseCameraCapture)
                    }
                )
            }
        }
    }
}

@Serializable
object IdentityVerification

@Preview
@Composable
private fun IdentityVerificationScreenPreview() {
    VotumTheme {
        IdentityVerificationContent(
            state = IdentityVerificationScreenState(
                nationalId = "1234567890",
                verificationCode = "VER123456"
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun IdentityVerificationScreenWithDocumentPreview() {
    VotumTheme {
        IdentityVerificationContent(
            state = IdentityVerificationScreenState(
                nationalId = "1234567890",
                verificationCode = "VER123456",
                documentProof = "data:image/jpeg;base64,placeholder",
                documentFileName = "national_id.jpg",
                documentFileSize = 1024 * 512
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun IdentityVerificationScreenLoadingPreview() {
    VotumTheme {
        IdentityVerificationContent(
            state = IdentityVerificationScreenState(
                nationalId = "1234567890",
                verificationCode = "VER123456",
                documentProof = "data:image/jpeg;base64,placeholder",
                documentFileName = "national_id.jpg",
                documentFileSize = 1024 * 512,
                isLoading = true
            ),
            onIntent = {}
        )
    }
}
