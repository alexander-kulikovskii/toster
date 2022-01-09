plugins {
    id("com.android.library")
    id("kotlin-android")
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("kotlinx-serialization")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    api("io.kotest:kotest-runner-junit5:4.6.2")
    implementation("com.lordcodes.turtle:turtle:0.6.0")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")

    testImplementation("io.kotlintest:kotlintest-runner-junit4:3.4.2")
}

android {
    compileSdkVersion(31)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(31)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
