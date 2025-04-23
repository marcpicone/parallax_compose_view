rootProject.buildFileName = "build.gradle.kts"
include(
    ":feature_parallax_view_sample_app",
    ":feature_parallax_view"
)

// https://github.com/JetBrains/compose-jb/blob/master/tutorials/Getting_Started/README.md
pluginManagement {
    plugins {

        // [Announce] https://android-developers.googleblog.com/2021/02/announcing-kotlin-symbol-processing-ksp.html
        // [v1] https://android-developers.googleblog.com/2021/09/accelerated-kotlin-build-times-with.html
        // [SupportedLib] https://kotlinlang.org/docs/ksp-overview.html#supported-libraries
        // [HowToUse] https://github.com/google/ksp
        id("com.google.devtools.ksp") version "1.6.20-1.0.5"

        // https://kotlinlang.org/docs/kapt.html
        kotlin("kapt") version "1.6.20"
    }
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
