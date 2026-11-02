plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "io.github.vedantjoshi23.kairos"
    compileSdk = 35
    defaultConfig {
        applicationId = "io.github.vedantjoshi23.kairos"
        minSdk = 26
        // TD14: raise to Play's current requirement before the first public release.
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-slice1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core:kernel"))
    implementation(project(":core:eventbus"))
    implementation(project(":core:config"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:trip:api"))
    implementation(project(":feature:trip:domain"))
    implementation(project(":feature:trip:platform"))
    implementation(project(":feature:trip:ui"))
    implementation(project(":feature:location:api"))
    implementation(project(":feature:location:platform"))
    implementation(project(":feature:location:ui"))
    implementation(project(":feature:map:api"))
    implementation(project(":feature:map:ui"))
    implementation(project(":feature:map:platform"))
    implementation(project(":feature:map:domain"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.junit)
}
