package io.votum.app

import android.app.Application
import io.votum.app.presentation.utils.startKoinPlatform
import io.votum.core.presentation.utils.PlatformContext

class VotumApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoinPlatform(PlatformContext(this))
    }
}
