package io.votum.core.presentation.utils

import kotlin.coroutines.cancellation.CancellationException

inline fun <T> Result<T>.onFailureRethrowCancellation(action: (Throwable) -> Unit): Result<T> {
    return onFailure { throwable ->
        if (throwable is CancellationException) throw throwable
        action(throwable)
    }
}
