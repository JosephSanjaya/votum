package io.votum.app.presentation.navigation

import androidx.navigation.NavController
import io.votum.core.presentation.navigation.NavigationEvent

abstract class NavigationEventHandler {
    abstract fun canHandle(event: NavigationEvent): Boolean
    abstract fun navigate(navController: NavController, event: NavigationEvent)
}
