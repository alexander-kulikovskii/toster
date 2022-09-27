@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(coreLibs.plugins.kotlin.lib.get().pluginId)
    id(coreLibs.plugins.kotlin.android.get().pluginId)
    id(coreLibs.plugins.kotlin.serialization.get().pluginId)
    id(coreLibs.plugins.pitest.get().pluginId)
    id(coreLibs.plugins.dexcount.get().pluginId)
    alias(coreLibs.plugins.kover)
    alias(coreLibs.plugins.detekt)
}
apply {
    from("${rootDir}/gradle/dependency-updates.gradle")
}

dependencies {
    implementation(coreLibs.kotlinx.serialization)
    api(coreLibs.kotest.runner)
    implementation(coreLibs.turtle)
    implementation(coreLibs.kotlinx.html)
//    {
//        exclude(group = "org.jetbrains.kotlin")
//    }

    detektPlugins(coreLibs.detekt.formatting)

    testImplementation(testLibs.kotest.pitest.extension)
    testImplementation(testLibs.bundles.mockk)
    testImplementation(coreLibs.kotlin.reflect)
}

android {
    compileSdkVersion(33)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(33)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TOSTER_VERSION", "\"${coreLibs.versions.toster.get()}\"")
        buildConfigField("String", "CHART_VERSION", "\"${coreLibs.versions.chart.get()}\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}

project.apply{
    from("$rootDir/gradle/pitest.gradle")
    from("$rootDir/gradle/maven_publish.gradle")
}

configure<pl.droidsonroids.gradle.pitest.PitestPluginExtension> {
    testPlugin.set("Kotest")
    targetClasses.set(listOf("fi.epicbot.toster.*"))
}

kover {
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ)
    intellijEngineVersion.set(coreLibs.versions.kover.intellij.engine.get())
    jacocoEngineVersion.set(coreLibs.versions.kover.jacoco.engine.get())
}
