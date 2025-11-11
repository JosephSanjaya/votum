package io.votum.onboarding.data

import io.votum.onboarding.data.remote.TestRemoteApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.votum.onboarding.data.remote.createTestRemoteApi
import org.koin.core.annotation.Single

@Single
class TestRepository(ktorfit: Ktorfit) :
    TestRemoteApi by ktorfit.createTestRemoteApi()
