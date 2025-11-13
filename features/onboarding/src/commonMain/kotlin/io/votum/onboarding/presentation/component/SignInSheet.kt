/*
 * Copyright (c) 2025 Sanjaya Inc. All rights reserved.
 */

package io.votum.onboarding.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import io.votum.core.presentation.utils.CoreResources
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import votum.features.onboarding.generated.resources.Res
import votum.features.onboarding.generated.resources.cta_sign_in
import votum.features.onboarding.generated.resources.cta_sign_in_or_sign_up
import votum.features.onboarding.generated.resources.cta_sign_up
import votum.features.onboarding.generated.resources.subtitle_welcome
import votum.features.onboarding.generated.resources.title_welcome
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInSheet(
    isShown: Boolean,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    val wrappedOnDismissRequest: () -> Unit = {
        scope.launch {
            sheetState.hide()
            delay(200.milliseconds)
            onDismissRequest()
        }
    }
    AnimatedVisibility(
        visible = isShown,
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        ModalBottomSheet(wrappedOnDismissRequest, sheetState = sheetState) {
            SignInSheetContent(
                onSignInClick = onSignInClick,
                onSignUpClick = onSignUpClick,
                onBackClick = wrappedOnDismissRequest
            )
        }
    }
}

@Composable
private fun SignInSheetContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .padding(vertical = 24.dp, horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onBackClick,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(14.dp)
            ) {
                Image(
                    painterResource(CoreResources.icArrowForward),
                    contentDescription = stringResource(CoreResources.ctaSkip),
                    modifier = Modifier
                        .size(14.dp)
                        .rotate(180f),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
            Text(
                stringResource(Res.string.cta_sign_in_or_sign_up),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
        Text(
            stringResource(Res.string.title_welcome),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            stringResource(Res.string.subtitle_welcome),
            Modifier.padding(bottom = 160.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        AgreementText(
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onSignInClick, modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(Res.string.cta_sign_in),
                style = MaterialTheme.typography.titleMedium
            )
        }
        OutlinedButton(onSignUpClick, modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(Res.string.cta_sign_up),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SignInSheetPreview() {
    VotumTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SignInSheetContent()
        }
    }
}
