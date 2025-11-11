package io.votum.core.data

import android.content.Context.MODE_PRIVATE
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.votum.core.presentation.utils.PlatformContext
import org.koin.java.KoinJavaComponent
import kotlin.jvm.java

actual class LocalDataSource(
    private val context: PlatformContext
) : Settings by SharedPreferencesSettings(
    context.appContext.getSharedPreferences(
        "votum-pref",
        MODE_PRIVATE
    )
)

actual fun createLocalDataSource(): LocalDataSource {
    val context = KoinJavaComponent.get<PlatformContext>(PlatformContext::class.java)
    return LocalDataSource(context)
}
