package io.votum.app.presentation.utils

import io.votum.app.di.AppModule
import io.votum.core.di.CoreModules
import io.votum.core.presentation.utils.PlatformContext
import io.votum.onboarding.di.OnboardingModule
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module
import platform.Foundation.NSBundle

actual fun startKoinPlatform(context: PlatformContext?) {
    startKoin {
        modules(
            listOf(
                CoreModules.module,
                module {
                    single { context ?: PlatformContext(NSBundle.mainBundle()) }
                }
            ) + listOf(
                AppModule.module,
                OnboardingModule.module
            )
        )
    }
}
