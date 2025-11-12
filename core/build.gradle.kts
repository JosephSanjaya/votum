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
    val localPropertiesFile = rootProject.file("local.properties")
    require(localPropertiesFile.exists()) {
        "local.properties file not found. Please create local.properties file in the root directory."
    }
    
    val localPropertiesContent = localPropertiesFile.readText()
    val apiBaseUrlProperty = localPropertiesContent.lines()
        .find { line -> line.startsWith("api.base.url=") }
        ?.substringAfter("api.base.url=")
        ?.trim()
    
    require(!apiBaseUrlProperty.isNullOrBlank()) {
        "api.base.url property is missing or empty in local.properties. Please add: api.base.url"
    }
    
    buildConfigField(
        "String",
        "BASE_URL",
        "\"$apiBaseUrlProperty\""
    )
}
