package io.votum.onboarding.data.remote

import de.jensklingenberg.ktorfit.http.GET

interface TestRemoteApi {
    @GET("v1/test/success")
    suspend fun getTest(): String
}
