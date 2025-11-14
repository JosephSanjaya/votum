/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VerificationCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = "Verification Code") },
            placeholder = { Text(text = "Enter verification code") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            leadingIcon = {
                Text(
                    text = "🔐",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingText = if (errorMessage == null) {
                { Text("Enter the verification code provided to you") }
            } else {
                null
            }
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun VerificationCodeFieldPreview() {
    VotumTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            VerificationCodeField(
                value = "VER123456",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )

            VerificationCodeField(
                value = "",
                onValueChange = {},
                errorMessage = "Verification code is required"
            )
        }
    }
}
