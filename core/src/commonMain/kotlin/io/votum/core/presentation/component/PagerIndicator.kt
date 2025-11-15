/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.core.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.core.presentation.preview.VotumPreview

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage

            val width by animateDpAsState(
                targetValue = if (isSelected) 48.dp else 12.dp,
                label = "indicatorWidth"
            )

            val color by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                label = "indicatorColor"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .height(4.dp)
                    .width(width)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}

@Composable
@VotumPreview
private fun PagerIndicatorPreview() {
    VotumTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            PagerIndicator(3, 1)
        }
    }
}
