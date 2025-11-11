package io.votum.app.presentation.utils

import io.votum.app.di.AppModule
import io.votum.core.di.CoreModules
import io.votum.core.presentation.utils.PlatformContext
import io.votum.onboarding.di.OnboardingModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module

actual fun startKoinPlatform(context: PlatformContext?) {
    context?.let {
        startKoin {
            androidContext(context.appContext)
            androidLogger()
            modules(
                listOf(
                    CoreModules.module,
                    module {
                        single<PlatformContext> { context }
                    }
                ) + listOf(
                    AppModule.module,
                    OnboardingModule.module
                )
            )
        }
    }
}
