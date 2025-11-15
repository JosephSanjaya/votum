/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.votum.election.domain.model.ElectionStatus
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun ElectionTimeInfo(
    startTime: Instant,
    endTime: Instant,
    status: ElectionStatus,
    modifier: Modifier = Modifier
) {
    val now = Clock.System.now()

    Column(modifier = modifier) {
        when {
            now < startTime -> {
                val timeUntilStart = startTime - now
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⏰",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Starts in ${timeUntilStart.formatDuration()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            now in startTime..endTime && status == ElectionStatus.VOTING_ACTIVE -> {
                val timeRemaining = endTime - now
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⏳",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${timeRemaining.formatDuration()} remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            else -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏁",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Voting Ended",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun Duration.formatDuration(): String {
    val totalDays = inWholeDays
    val totalHours = inWholeHours
    val totalMinutes = inWholeMinutes

    return when {
        totalDays > 0 -> "$totalDays day${if (totalDays > 1) "s" else ""}"
        totalHours > 0 -> "$totalHours hour${if (totalHours > 1) "s" else ""}"
        totalMinutes > 0 -> "$totalMinutes minute${if (totalMinutes > 1) "s" else ""}"
        else -> "Less than a minute"
    }
}
