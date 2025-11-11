plugins {
    alias(sjy.plugins.buildlogic.multiplatform.app)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
    }
}

android {
    namespace = "io.votum.app"
    defaultConfig {
        applicationId = "io.votum.app"
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
