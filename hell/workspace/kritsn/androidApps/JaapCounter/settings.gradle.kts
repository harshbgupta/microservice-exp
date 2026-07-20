@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        // Pin plugin versions to avoid "already on the classpath with an unknown version" errors
        id("com.android.application") version "8.3.2" apply false
        id("com.android.library") version "8.3.2" apply false
        id("org.jetbrains.kotlin.jvm") version "1.9.0" apply false
        id("org.jetbrains.kotlin.android") version "1.9.0" apply false
        id("com.google.dagger.hilt.android") version "2.48" apply false
        id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
        id("org.jetbrains.kotlin.plugin.parcelize") version "1.9.0" apply false
        id("com.google.gms.google-services") version "4.4.1" apply false
        id("com.google.firebase.crashlytics") version "3.0.1" apply false
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application", "com.android.library" -> useVersion("8.3.2")
                "org.jetbrains.kotlin.jvm", "org.jetbrains.kotlin.android", "org.jetbrains.kotlin.kapt", "org.jetbrains.kotlin.plugin.parcelize" -> useVersion("1.9.0")
                "com.google.dagger.hilt.android" -> useVersion("2.48")
                "androidx.navigation.safeargs.kotlin" -> useVersion("2.7.7")
                "com.google.gms.google-services" -> useVersion("4.4.1")
                "com.google.firebase.crashlytics" -> useVersion("3.0.1")
                else -> { /* leave default */ }
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "JaapCounter"
include(":app")
include(":kernel")
