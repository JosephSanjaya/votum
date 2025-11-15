/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.result.domain.model.CandidateResult
import io.votum.core.presentation.preview.VotumPreview

@Composable
fun CandidateResultCard(
    candidateResult: CandidateResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = if (candidateResult.isWinner) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (candidateResult.isWinner) {
                    Text(
                        text = "🏆",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = candidateResult.candidateName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${candidateResult.votePercentage.formatPercentage()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (candidateResult.isWinner) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = "${candidateResult.voteCount.formatWithCommas()} votes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (candidateResult.votePercentage / 100).toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = if (candidateResult.isWinner) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            )
        }
    }
}

private fun Int.formatWithCommas(): String {
    return toString().reversed().chunked(3).joinToString(",").reversed()
}

private fun Double.formatPercentage(): String {
    val rounded = (this * 10).toInt() / 10.0
    return rounded.toString()
}

@VotumPreview
@Composable
private fun CandidateResultCardWinnerPreview() {
    VotumTheme {
        CandidateResultCard(
            candidateResult = CandidateResult(
                candidateId = "candidate-1",
                candidateName = "Alice Johnson",
                voteCount = 8234,
                votePercentage = 53.4,
                isWinner = true
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@VotumPreview
@Composable
private fun CandidateResultCardPreview() {
    VotumTheme {
        CandidateResultCard(
            candidateResult = CandidateResult(
                candidateId = "candidate-2",
                candidateName = "Bob Smith",
                voteCount = 7186,
                votePercentage = 46.6,
                isWinner = false
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
