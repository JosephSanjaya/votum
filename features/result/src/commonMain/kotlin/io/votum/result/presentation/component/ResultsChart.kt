/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.result.domain.model.CandidateResult
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import io.votum.core.presentation.preview.VotumPreview

@Composable
fun ResultsChart(
    candidateResults: PersistentList<CandidateResult>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 Vote Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val maxVotes = candidateResults.maxOfOrNull { it.voteCount } ?: 1

            candidateResults.forEach { candidate ->
                CandidateBarItem(
                    candidate = candidate,
                    maxVotes = maxVotes
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun CandidateBarItem(
    candidate: CandidateResult,
    maxVotes: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (candidate.isWinner) {
                    Text(
                        text = "🏆",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = candidate.candidateName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (candidate.isWinner) FontWeight.Bold else FontWeight.Normal
                )
            }

            Text(
                text = "${candidate.voteCount.formatWithCommas()} (${candidate.votePercentage.formatPercentage()}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            val barWidth = (candidate.voteCount.toFloat() / maxVotes.toFloat())
            Box(
                modifier = Modifier
                    .fillMaxWidth(barWidth)
                    .height(24.dp)
                    .background(
                        color = if (candidate.isWinner) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
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
private fun ResultsChartPreview() {
    VotumTheme {
        ResultsChart(
            candidateResults = persistentListOf(
                CandidateResult(
                    candidateId = "candidate-1",
                    candidateName = "Alice Johnson",
                    voteCount = 8234,
                    votePercentage = 53.4,
                    isWinner = true
                ),
                CandidateResult(
                    candidateId = "candidate-2",
                    candidateName = "Bob Smith",
                    voteCount = 7186,
                    votePercentage = 46.6,
                    isWinner = false
                )
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
