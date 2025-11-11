package io.votum.core.presentation.utils

import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import io.votum.core.presentation.component.SnackbarEventBus
import io.votum.core.presentation.navigation.NavigationEvent
import io.votum.core.presentation.navigation.NavigationEventBus
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform.getKoin
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.coroutines.cancellation.CancellationException

@Stable
fun interface EventReceiver<Event : Any> {
    fun onEvent(event: Event)
}

abstract class BaseViewModel<State : Any, Event : Any>(
    initialState: State,
    val navigationEventBus: NavigationEventBus = getKoin().get(),
    val snackbarEventBus: SnackbarEventBus = getKoin().get(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val exceptionHandler: ExceptionHandler<State, Event> = DefaultExceptionHandler(),
    private val onCreateHandler: OnCreateHandler<State, Event> = DefaultOnCreateHandler()
) : ContainerHost<State, Event>, EventReceiver<Event>, ViewModel() {

    init {
        exceptionHandler.withViewModel(this)
        onCreateHandler.withViewModel(this)
    }

    override val container: Container<State, Event> =
        container(
            initialState = initialState,
            buildSettings = {
                this.exceptionHandler = this@BaseViewModel.exceptionHandler
            },
            onCreate = {
                onCreateHandler.onCreate()
            }
        )

    suspend fun <T> safeIoCall(
        action: suspend () -> T
    ): T? {
        return withContext(ioDispatcher) {
            try {
                action()
            } catch (e: CancellationException) {
                throw e
            } catch (e: ClientRequestException) {
                exceptionHandler.handleException(currentCoroutineContext(), e)
                null
            } catch (e: ServerResponseException) {
                exceptionHandler.handleException(currentCoroutineContext(), e)
                null
            } catch (e: ResponseException) {
                exceptionHandler.handleException(currentCoroutineContext(), e)
                null
            } catch (e: Throwable) {
                exceptionHandler.handleException(currentCoroutineContext(), e)
                null
            }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is SnackbarVisuals -> snackbarEventBus.post(event)
            is NavigationEvent -> navigationEventBus.post(event)
        }
    }
}
