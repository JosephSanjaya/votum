/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.onboarding.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.preview.BooleanPreviewProvider
import io.votum.core.presentation.preview.VotumPreview
import io.votum.core.presentation.theme.VotumTheme
import io.votum.core.presentation.utils.CoreResources
import org.jetbrains.compose.resources.stringResource
import votum.features.onboarding.generated.resources.Res
import votum.features.onboarding.generated.resources.cta_sign_in_or_sign_up

@Composable
fun OnboardFooter(
    isLastStep: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val text = remember(isLastStep) {
        if (isLastStep) {
            Res.string.cta_sign_in_or_sign_up
        } else {
            CoreResources.ctaContinue
        }
    }
    Button(
        onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            stringResource(text),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onPrimary
            ),
        )
    }
}

@Composable
@VotumPreview
private fun OnboardFooterPreview(
    @Suppress("Deprecation")
    @org.jetbrains.compose.ui.tooling.preview.PreviewParameter(
        BooleanPreviewProvider::class
    ) isLastStep: Boolean
) {
    VotumTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            OnboardFooter(
                isLastStep
            )
        }
    }
}
