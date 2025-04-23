// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        // Max stable version allowed by Kotlin multiplatform with Kotlin 2.0.20
        // https://kotlinlang.org/docs/multiplatform-compatibility-guide.html#version-compatibility
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath(kotlin("gradle-plugin", version = "2.0.20"))
        classpath("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.0.20")
    }
}

plugins {
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" // this version matches your Kotlin version
    kotlin("plugin.serialization") version "2.0.20"
    // https://github.com/google/ksp/releases, this version matches your Kotlin version
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    // KtLint - Static code analysis
    // https://github.com/pinterest/ktlint/releases
    val ktlint by configurations.creating

    dependencies {
        // KtLint - Static code analysis
        // https://github.com/pinterest/ktlint/releases
        ktlint("com.pinterest:ktlint:0.47.1")
    }

    // KtLint - Static code analysis
    // https://github.com/pinterest/ktlint/releases
    tasks.register<JavaExec>("ktlint") {
        group = "verification"
        description = "Check Kotlin code style."
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        args("--android", "src/**/*.kt")
    }

    // KtLint - Static code format
    // https://github.com/pinterest/ktlint/releases
    tasks.register<JavaExec>("ktformat") {
        group = "verification"
        description = "Check Kotlin code style."
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        args("--android", "-F", "src/**/*.kt")
    }
}
