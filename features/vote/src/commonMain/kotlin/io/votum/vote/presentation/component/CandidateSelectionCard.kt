/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.vote.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.election.domain.model.Candidate
import io.votum.core.presentation.preview.VotumPreview

@Composable
fun CandidateSelectionCard(
    candidate: Candidate,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CardDefaults.shape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onSelect),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 3.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = candidate.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = candidate.party,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = candidate.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@VotumPreview
@Composable
private fun CandidateSelectionCardPreview() {
    VotumTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CandidateSelectionCard(
                candidate = Candidate(
                    id = "1",
                    name = "Alice Johnson",
                    party = "Progressive Party",
                    description = "Experienced leader focused on economic reform and social justice",
                    imageUrl = null,
                    blockchainIndex = 0,
                    manifesto = null,
                    qualifications = emptyList()
                ),
                isSelected = false,
                onSelect = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            CandidateSelectionCard(
                candidate = Candidate(
                    id = "2",
                    name = "Bob Smith",
                    party = "Conservative Alliance",
                    description = "Advocate for traditional values and fiscal responsibility",
                    imageUrl = null,
                    blockchainIndex = 1,
                    manifesto = null,
                    qualifications = emptyList()
                ),
                isSelected = true,
                onSelect = {}
            )
        }
    }
}
