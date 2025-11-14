/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.result.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TurnoutIndicator(
    turnoutPercentage: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            progress = { (turnoutPercentage / 100).toFloat() },
            modifier = Modifier.size(80.dp),
            strokeWidth = 8.dp,
            color = when {
                turnoutPercentage >= 70 -> MaterialTheme.colorScheme.primary
                turnoutPercentage >= 50 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.error
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${turnoutPercentage.formatPercentage()}%",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Voter Turnout",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun Double.formatPercentage(): String {
    val rounded = (this * 10).toInt() / 10.0
    return rounded.toString()
}

@Preview
@Composable
private fun TurnoutIndicatorHighPreview() {
    VotumTheme {
        TurnoutIndicator(turnoutPercentage = 75.5)
    }
}

@Preview
@Composable
private fun TurnoutIndicatorMediumPreview() {
    VotumTheme {
        TurnoutIndicator(turnoutPercentage = 61.7)
    }
}

@Preview
@Composable
private fun TurnoutIndicatorLowPreview() {
    VotumTheme {
        TurnoutIndicator(turnoutPercentage = 42.3)
    }
}
