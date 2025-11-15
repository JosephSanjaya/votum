/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.onboarding.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.component.PagerIndicator
import io.votum.core.presentation.preview.PositionPreviewProvider
import io.votum.core.presentation.theme.VotumTheme
import io.votum.core.presentation.utils.CoreResources
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import io.votum.core.presentation.preview.VotumPreview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

@Composable
fun OnboardHeader(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    onSkipClicked: () -> Unit = {}
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PagerIndicator(pageCount, currentPage, modifier = Modifier.padding(start = 16.dp))
        TextButton(onSkipClicked) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(CoreResources.ctaSkip),
                    modifier = Modifier.padding(end = 8.dp),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Image(
                    painterResource(CoreResources.icArrowForward),
                    contentDescription = stringResource(CoreResources.ctaSkip),
                    modifier = Modifier.size(14.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}

@Composable
@VotumPreview
private fun OnboardHeaderPreview(
    @Suppress("Deprecation")
    @PreviewParameter(PositionPreviewProvider::class) currentPage: Int
) {
    VotumTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            OnboardHeader(
                3,
                currentPage
            )
        }
    }
}
