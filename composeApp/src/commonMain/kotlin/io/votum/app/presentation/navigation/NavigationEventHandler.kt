package io.votum.app.presentation.navigation

import androidx.navigation.NavController
import io.votum.core.presentation.navigation.NavigationIntent

abstract class NavigationEventHandler {
    abstract fun canHandle(event: NavigationIntent): Boolean
    abstract fun navigate(navController: NavController, event: NavigationIntent)
}
