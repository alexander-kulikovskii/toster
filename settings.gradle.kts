enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

apply(from = "gradle/dependency-catalogs.settings.gradle.kts")

rootProject.name = "Toster"
include(":lib")
include(":samples")
