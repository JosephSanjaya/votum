/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.component

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
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.core.presentation.preview.VotumPreview

@Composable
fun CertificationBadge(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "✅",
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Certified",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@VotumPreview
@Composable
private fun CertificationBadgePreview() {
    VotumTheme {
        CertificationBadge()
    }
}
