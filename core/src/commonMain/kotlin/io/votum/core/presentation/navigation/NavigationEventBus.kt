package io.votum.core.presentation.navigation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

interface NavigationEvent

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
@Single
class NavigationEventBus(
    private val _flow: MutableSharedFlow<NavigationEvent> = MutableSharedFlow(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : SharedFlow<NavigationEvent> by _flow,
    CoroutineScope by CoroutineScope(dispatcher) {
    fun post(event: NavigationEvent) = launch {
        _flow.emit(event)
    }
}
