package io.votum.app

import androidx.compose.runtime.Composable
import io.votum.app.presentation.navigation.VotumNavHost
import io.votum.core.presentation.theme.VotumTheme
import io.votum.core.presentation.preview.VotumPreview

@Composable
@VotumPreview
fun App() {
    VotumTheme {
        VotumNavHost()
    }
}
