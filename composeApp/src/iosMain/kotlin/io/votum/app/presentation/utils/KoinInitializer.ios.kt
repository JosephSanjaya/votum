package io.votum.app.presentation.utils

import io.votum.app.di.AppModule
import io.votum.auth.di.AuthModule
import io.votum.core.di.CoreModules
import io.votum.core.presentation.utils.PlatformContext
import io.votum.election.di.ElectionModule
import io.votum.identity.di.IdentityModule
import io.votum.onboarding.di.OnboardingModule
import io.votum.registration.di.RegistrationModule
import io.votum.result.di.ResultModule
import io.votum.vote.di.VoteModule
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
                AuthModule.module,
                ElectionModule.module,
                IdentityModule.module,
                OnboardingModule.module,
                RegistrationModule.module,
                ResultModule.module,
                VoteModule.module,
            )
        )
    }
}
