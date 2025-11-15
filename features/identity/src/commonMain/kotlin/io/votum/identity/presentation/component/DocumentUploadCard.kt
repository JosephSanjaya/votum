/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */
package io.votum.identity.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.core.presentation.preview.VotumPreview

@Composable
fun DocumentUploadCard(
    onUploadClick: () -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (errorMessage != null) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📄",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Upload Identity Document",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Upload a clear photo of your government-issued ID",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onUploadClick,
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                ) {
                    Text(text = "📁")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Choose File")
                }

                OutlinedButton(
                    onClick = onCameraClick,
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                ) {
                    Text(text = "📷")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Take Photo")
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Supported formats: JPEG, PNG, PDF (Max 5MB)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@VotumPreview
@Composable
private fun DocumentUploadCardPreview() {
    VotumTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DocumentUploadCard(
                onUploadClick = {},
                onCameraClick = {}
            )

            DocumentUploadCard(
                onUploadClick = {},
                onCameraClick = {},
                errorMessage = "Document proof is required"
            )
        }
    }
}
