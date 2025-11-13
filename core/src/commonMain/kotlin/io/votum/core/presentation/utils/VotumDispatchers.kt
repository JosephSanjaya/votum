package io.votum.core.presentation.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Single

@Single
class VotumDispatchers {
    val main = Dispatchers.Main
    val io = Dispatchers.IO
    val default = Dispatchers.Default
}
