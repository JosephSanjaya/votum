package io.votum.core.presentation.utils

import io.votum.core.presentation.component.DefaultSnackBarVisuals
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

abstract class ExceptionHandler<State : Any, Event : Any> : CoroutineExceptionHandler {
    internal lateinit var host: BaseViewModel<State, Event>
        private set

    internal fun withViewModel(host: BaseViewModel<State, Event>) {
        this.host = host
    }

    companion object {
        fun <State : Any, Event : Any> create(
            onException: BaseViewModel<State, Event>.(Throwable) -> Unit
        ): ExceptionHandler<State, Event> {
            return object : ExceptionHandler<State, Event>() {
                override fun handleException(
                    context: CoroutineContext,
                    exception: Throwable
                ) {
                    onException(host, exception)
                }

                override val key: CoroutineContext.Key<*>
                    get() = CoroutineExceptionHandler
            }
        }
    }
}

class DefaultExceptionHandler<State : Any, Event : Any> :
    ExceptionHandler<State, Event>(),
    CoroutineExceptionHandler {

    override fun handleException(
        context: CoroutineContext,
        exception: Throwable
    ) {
        VotumLogger.e(TAG, exception.message.orEmpty())
        host.snackbarEventBus.post(DefaultSnackBarVisuals(message = exception.message.orEmpty()))
    }

    override val key: CoroutineContext.Key<*>
        get() = CoroutineExceptionHandler

    companion object {
        private const val TAG = "DefaultExceptionHandler"
    }
}
