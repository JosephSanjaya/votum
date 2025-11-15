/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.vote.domain.model.VoteVerification
import io.votum.core.presentation.preview.VotumPreview

@Composable
fun VerificationResultCard(
    verification: VoteVerification,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (verification.isValid) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (verification.isValid) "✅" else "❌",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (verification.isValid) "Vote Verified" else "Verification Failed",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (verification.isValid) {
                            "Your vote has been confirmed on the blockchain"
                        } else {
                            "Unable to verify vote on blockchain"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (verification.isValid) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                VerificationDetailRow(
                    label = "Block Number",
                    value = verification.blockNumber
                )

                Spacer(modifier = Modifier.height(8.dp))

                VerificationDetailRow(
                    label = "Timestamp",
                    value = verification.timestamp
                )

                Spacer(modifier = Modifier.height(8.dp))

                VerificationDetailRow(
                    label = "Voter Public Key",
                    value = verification.voterPublicKey.take(16) + "...",
                    isMonospace = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                VerificationDetailRow(
                    label = "Candidate Index",
                    value = verification.candidateIndex.toString()
                )

                Spacer(modifier = Modifier.height(8.dp))

                VerificationDetailRow(
                    label = "Verification Proof",
                    value = verification.verificationProof.take(16) + "...",
                    isMonospace = true
                )
            }
        }
    }
}

@Composable
private fun VerificationDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isMonospace: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default
        )
    }
}

@VotumPreview
@Composable
private fun VerificationResultCardValidPreview() {
    VotumTheme {
        VerificationResultCard(
            verification = VoteVerification(
                isValid = true,
                transactionHash = "0xabcdef1234567890",
                blockNumber = "1234567",
                timestamp = "2024-01-15T11:15:00Z",
                voterPublicKey = "0x1234567890abcdef",
                candidateIndex = 0,
                verificationProof = "0xabcdef1234567890"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@VotumPreview
@Composable
private fun VerificationResultCardInvalidPreview() {
    VotumTheme {
        VerificationResultCard(
            verification = VoteVerification(
                isValid = false,
                transactionHash = "0xabcdef1234567890",
                blockNumber = "",
                timestamp = "",
                voterPublicKey = "",
                candidateIndex = 0,
                verificationProof = ""
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
