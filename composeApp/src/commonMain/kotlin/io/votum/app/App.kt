package io.votum.app

import androidx.compose.runtime.Composable
import io.votum.app.presentation.navigation.VotumNavHost
import io.votum.core.presentation.theme.VotumTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    VotumTheme {
        VotumNavHost()
    }
}
