package io.votum.app.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.votum.core.presentation.theme.VotumTheme
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import io.votum.core.presentation.preview.VotumPreview
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import votum.composeapp.generated.resources.Res
import votum.composeapp.generated.resources.compose_multiplatform

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewModel: SplashScreenViewModel = koinViewModel(),
) {
    val state = viewModel.collectAsState().value
    SplashScreenContent(uiState = state, modifier = modifier)
}

@Composable
private fun SplashScreenContent(
    uiState: SplashScreenUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        Res.drawable.compose_multiplatform
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(155.dp)
                )
                Text(
                    uiState.title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Serializable
object Splash

@Composable
@VotumPreview
private fun SplashScreenPreview() {
    VotumTheme {
        SplashScreenContent(
            modifier = Modifier.background(Color.White),
            uiState = SplashScreenUiState()
        )
    }
}
