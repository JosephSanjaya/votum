package io.votum.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import io.votum.core.presentation.navigation.NavigationIntent
import io.votum.core.presentation.navigation.NavigationEventBus
import org.koin.compose.koinInject
import org.koin.core.annotation.Single

@Single
class NavigationEventHandlerRegistry(
    private val handlers: List<NavigationEventHandler>
) {
    fun handle(navigator: NavController, event: NavigationIntent) {
        handlers.firstOrNull { it.canHandle(event) }?.navigate(navigator, event)
    }
}

@Composable
fun NavigationEventBusHandler(
    navController: NavController,
    eventBus: NavigationEventBus = koinInject(),
    navigationEventHandlerRegistry: NavigationEventHandlerRegistry = koinInject()
) {
    LaunchedEffect(Unit) {
        eventBus.collect {
            navigationEventHandlerRegistry.handle(navController, it)
        }
    }
}
