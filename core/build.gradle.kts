plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
    alias(sjy.plugins.buildconfig.kmp)
}

android {
    namespace = "io.votum.core"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

buildConfig {
    buildConfigField(
        "String",
        "BASE_URL",
        "\"https://wandr-api-production.up.railway.app/\""
    )
}
