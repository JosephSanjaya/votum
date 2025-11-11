/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.onboarding.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.votum.core.presentation.preview.PositionPreviewProvider
import io.votum.core.presentation.theme.VotumTheme
import io.votum.onboarding.presentation.screen.OnboardingScreenUiState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

@Composable
fun OnboardStep(
    illustrationRes: DrawableResource,
    titleRes: StringResource,
    descriptionRes: StringResource,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(illustrationRes),
            contentDescription = stringResource(titleRes),
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 4.dp)
        )
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = stringResource(descriptionRes),
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun OnboardStepPreview(
    @PreviewParameter(PositionPreviewProvider::class) currentPage: Int
) {
    VotumTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            val data = OnboardingScreenUiState.OnboardContent.default()[currentPage]
            OnboardStep(
                illustrationRes = data.illustrationRes,
                titleRes = data.titleRes,
                descriptionRes = data.descriptionRes
            )
        }
    }
}
