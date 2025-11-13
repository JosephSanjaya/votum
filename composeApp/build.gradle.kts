plugins {
    alias(sjy.plugins.buildlogic.multiplatform.app)
    alias(sjy.plugins.buildlogic.multiplatform.cmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":features:onboarding"))
            implementation(project(":features:registration"))
            implementation(project(":features:auth"))
            implementation(project(":features:election"))
            implementation(project(":features:identity"))
            implementation(project(":features:result"))
            implementation(project(":features:vote"))
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
