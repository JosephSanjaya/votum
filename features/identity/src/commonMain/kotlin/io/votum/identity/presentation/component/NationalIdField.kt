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
fun NationalIdField(
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
            label = { Text(text = "National ID") },
            placeholder = { Text(text = "Enter your National ID") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.clearFocus() }
            ),
            leadingIcon = {
                Text(
                    text = "🆔",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingText = if (errorMessage == null) {
                { Text("Enter your National ID (10-20 alphanumeric characters)") }
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
private fun NationalIdFieldPreview() {
    VotumTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            NationalIdField(
                value = "NAT1234567890",
                onValueChange = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NationalIdField(
                value = "123",
                onValueChange = {},
                errorMessage = "National ID must be at least 10 characters"
            )
        }
    }
}
