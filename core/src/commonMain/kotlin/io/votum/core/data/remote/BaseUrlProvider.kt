package io.votum.core.data.remote

import Votum.core.BuildConfig
import org.koin.core.annotation.Single

@Single
class BaseUrlProvider {
    fun getBaseUrl(): String {
        return BuildConfig.BASE_URL
    }
}
