package io.votum.core.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.votum.core.data.LocalDataSource
import io.votum.core.data.createLocalDataSource
import io.votum.core.data.remote.KtorfitCreator
import io.votum.core.data.util.JsonParserCreator
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@ComponentScan("io.votum.core")
@Module
object CoreModules {
    @Single
    fun provideJsonParser(jsonParserCreator: JsonParserCreator): Json {
        return jsonParserCreator.create()
    }

    @Single
    fun provideLocalDataSource(): LocalDataSource {
        return createLocalDataSource()
    }

    @Single
    fun provideKtorfit(creator: KtorfitCreator): Ktorfit {
        return creator.create()
    }
}
