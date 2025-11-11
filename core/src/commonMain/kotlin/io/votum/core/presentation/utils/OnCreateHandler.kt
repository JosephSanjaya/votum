package io.votum.core.presentation.utils

abstract class OnCreateHandler<State : Any, Event : Any> {
    internal lateinit var host: BaseViewModel<State, Event>
        private set
    internal fun withViewModel(host: BaseViewModel<State, Event>) {
        this.host = host
    }

    abstract fun onCreate()

    companion object {
        fun <State : Any, Event : Any> create(
            onCreateCallback: BaseViewModel<State, Event>.() -> Unit
        ): OnCreateHandler<State, Event> {
            return object : OnCreateHandler<State, Event>() {
                override fun onCreate() {
                    onCreateCallback(host)
                }
            }
        }
    }
}

class DefaultOnCreateHandler<State : Any, Event : Any> :
    OnCreateHandler<State, Event>() {
    override fun onCreate() {
        VotumLogger.i(TAG, "Starting ViewModel")
    }

    companion object {
        private const val TAG = "DefaultViewModelCreateHandler"
    }
}
