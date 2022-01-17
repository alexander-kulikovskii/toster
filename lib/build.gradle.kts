plugins {
    id("com.android.library")
    id("kotlin-android")
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("kotlinx-serialization")
    id("maven-publish")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    api("io.kotest:kotest-runner-junit5:4.6.2")
    implementation("com.lordcodes.turtle:turtle:0.6.0")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")

    implementation("io.kotlintest:kotlintest-runner-junit4:3.4.2")
}

android {
    compileSdkVersion(31)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(31)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}

fun getVersionName(): String {
    return "0.1.0"
}

fun getArtificatId(): String {
    return "toster"
}

publishing {
    publications {
        create<MavenPublication>("release") {
            run {
                groupId = "fi.epicbot"
                artifactId = getArtificatId()
                version = getVersionName()
                artifact("$buildDir/outputs/aar/lib-release.aar")
            }
        }
    }
}
