/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.onboarding.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import io.votum.core.presentation.theme.VotumTheme
import io.votum.core.presentation.preview.VotumPreview

@Composable
fun AgreementText(
    modifier: Modifier = Modifier,
    onTermsClicked: () -> Unit = {},
    onPrivacyClicked: () -> Unit = {},
) {
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val annotatedText = buildAnnotatedString {
        append("By continuing, you agree to our ")

        pushStringAnnotation(tag = "terms", annotation = "terms")
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("Terms of Service")
        }
        pop()

        append(" and ")

        pushStringAnnotation(tag = "privacy", annotation = "privacy")
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("Privacy Policy")
        }
        pop()
    }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { pos ->
                layoutResult?.let { layout ->
                    val index = layout.getOffsetForPosition(pos)
                    annotatedText.getStringAnnotations(start = index, end = index)
                        .firstOrNull()?.let { ann ->
                            when (ann.tag) {
                                "terms" -> onTermsClicked()
                                "privacy" -> onPrivacyClicked()
                            }
                        }
                }
            }
        },
        onTextLayout = { layoutResult = it }
    )
}

@Composable
@VotumPreview
private fun AgreementTextPreview() {
    VotumTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            AgreementText()
        }
    }
}
