plugins {
    alias(sjy.plugins.buildlogic.multiplatform.lib)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
        }
    }
}

android {
    namespace = "io.votum.result"
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
