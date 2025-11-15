/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.component

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.result.domain.model.ElectionResults
import kotlinx.collections.immutable.persistentListOf
import io.votum.core.presentation.preview.VotumPreview

@Composable
fun ResultCard(
    results: ElectionResults,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Election Results",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (results.isFinalized) {
                    CertificationBadge()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ResultStatItem(
                    icon = "🗳️",
                    label = "Total Votes",
                    value = results.totalVotesCast.formatWithCommas()
                )

                ResultStatItem(
                    icon = "👥",
                    label = "Turnout",
                    value = "${results.turnoutPercentage.formatPercentage()}%"
                )

                ResultStatItem(
                    icon = "📈",
                    label = "Eligible Voters",
                    value = results.totalEligibleVoters.formatWithCommas()
                )
            }

            if (results.winner != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏆",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Winner",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = results.winner.candidateName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultStatItem(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
private fun ResultCardPreview() {
    VotumTheme {
        ResultCard(
            results = ElectionResults(
                electionId = "election-1",
                totalVotesCast = 15420,
                totalEligibleVoters = 25000,
                turnoutPercentage = 61.68,
                candidateResults = persistentListOf(),
                winner = io.votum.result.domain.model.CandidateResult(
                    candidateId = "candidate-1",
                    candidateName = "Alice Johnson",
                    voteCount = 8234,
                    votePercentage = 53.4,
                    isWinner = true
                ),
                isFinalized = true,
                finalizedAt = "2024-01-15T12:00:00Z",
                blockchainProof = "0xabcdef1234567890"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
