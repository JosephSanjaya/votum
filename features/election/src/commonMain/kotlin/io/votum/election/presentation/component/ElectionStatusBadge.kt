/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.election.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.votum.election.domain.model.ElectionStatus

@Composable
fun ElectionStatusBadge(
    status: ElectionStatus,
    modifier: Modifier = Modifier
) {
    val (emoji, text, backgroundColor) = when (status) {
        ElectionStatus.VOTING_ACTIVE -> Triple(
            "✅",
            "Voting Open",
            MaterialTheme.colorScheme.primaryContainer
        )
        ElectionStatus.REGISTRATION_OPEN -> Triple(
            "📝",
            "Registration Open",
            MaterialTheme.colorScheme.tertiaryContainer
        )
        ElectionStatus.VOTING_CLOSED -> Triple(
            "🔒",
            "Voting Closed",
            MaterialTheme.colorScheme.surfaceVariant
        )
        ElectionStatus.FINALIZED -> Triple(
            "🏆",
            "Results Available",
            MaterialTheme.colorScheme.secondaryContainer
        )
        ElectionStatus.DRAFT -> Triple(
            "📋",
            "Draft",
            MaterialTheme.colorScheme.surfaceVariant
        )
    }

    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
