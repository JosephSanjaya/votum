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
import io.votum.vote.domain.model.VotingStatus
import io.votum.core.presentation.preview.VotumPreview

@Composable
fun VotingStatusCard(
    votingStatus: VotingStatus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (votingStatus.isVotingActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (votingStatus.isVotingActive) "🟢" else "🔴",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (votingStatus.isVotingActive) "Voting Active" else "Voting Closed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = formatRemainingTime(votingStatus.remainingTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Votes Cast",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = votingStatus.totalVotesCast.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Voting Progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${votingStatus.votingProgress.toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (votingStatus.votingProgress / 100.0).toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun formatRemainingTime(milliseconds: Long): String {
    if (milliseconds <= 0) return "Ended"

    val hours = milliseconds / (1000 * 60 * 60)
    val minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60)

    return when {
        hours > 24 -> "${hours / 24}d ${hours % 24}h left"
        hours > 0 -> "${hours}h ${minutes}m left"
        minutes > 0 -> "${minutes}m left"
        else -> "< 1m left"
    }
}

@VotumPreview
@Composable
private fun VotingStatusCardActivePreview() {
    VotumTheme {
        VotingStatusCard(
            votingStatus = VotingStatus(
                electionId = "1",
                isVotingActive = true,
                totalVotesCast = 15420,
                voterHasVoted = false,
                remainingTime = 86400000,
                votingProgress = 65.2
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@VotumPreview
@Composable
private fun VotingStatusCardClosedPreview() {
    VotumTheme {
        VotingStatusCard(
            votingStatus = VotingStatus(
                electionId = "1",
                isVotingActive = false,
                totalVotesCast = 25000,
                voterHasVoted = true,
                remainingTime = 0,
                votingProgress = 100.0
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
