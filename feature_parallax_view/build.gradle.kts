plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.marcpicone.feature_parallax_view"
    compileSdk = 35

    group = "com.marcpicone.feature_parallax_view"
    version = "1.0.0"

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(JavaVersion.VERSION_17.toString().toInt())
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.annotation:annotation:1.9.1")

    // JetPack Compose
    // https://developer.android.com/jetpack/androidx/releases/compose-kotlin#pre-release_kotlin_compatibility
    implementation(platform("androidx.compose:compose-bom:2024.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.animation:animation")
    // https://developer.android.com/jetpack/compose/tooling
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    // https://developer.android.com/jetpack/androidx/releases/compose-foundation
    implementation("androidx.compose.foundation:foundation")
}
