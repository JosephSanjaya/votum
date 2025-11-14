/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.vote.domain.model.VoteReceipt
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VoteReceiptCard(
    receipt: VoteReceipt,
    candidateName: String,
    onVerify: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✅",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Vote Successfully Submitted",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Your vote has been recorded on the blockchain",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            ReceiptDetailRow(
                label = "Candidate",
                value = candidateName
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReceiptDetailRow(
                label = "Transaction Hash",
                value = receipt.transactionHash.take(16) + "...",
                isMonospace = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReceiptDetailRow(
                label = "Block Number",
                value = receipt.blockNumber
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReceiptDetailRow(
                label = "Timestamp",
                value = receipt.timestamp
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReceiptDetailRow(
                label = "Verification Code",
                value = receipt.verificationCode,
                isMonospace = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📤 Share")
                }

                Button(
                    onClick = onVerify,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🔍 Verify")
                }
            }
        }
    }
}

@Composable
private fun ReceiptDetailRow(
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
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default
        )
    }
}

@Preview
@Composable
private fun VoteReceiptCardPreview() {
    VotumTheme {
        VoteReceiptCard(
            receipt = VoteReceipt(
                voteId = "vote-uuid-1",
                electionId = "election-uuid-1",
                candidateId = "candidate-uuid-1",
                transactionHash = "0xabcdef1234567890abcdef1234567890",
                blockNumber = "1234567",
                timestamp = "2024-01-15T11:15:00Z",
                verificationCode = "VERIFY_ABCD1234_567890"
            ),
            candidateName = "Alice Johnson",
            onVerify = {},
            onShare = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
