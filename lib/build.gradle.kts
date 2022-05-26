plugins {
    id("com.android.library")
    id("kotlin-android")
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("kotlinx-serialization")
    id ("pl.droidsonroids.pitest")
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
    id("com.getkeepsafe.dexcount")
}
apply {
    from("${rootDir}/gradle/dependency-updates.gradle")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    api("io.kotest:kotest-runner-junit5:5.2.2")
    implementation("com.lordcodes.turtle:turtle:0.6.0")
    implementation("io.kotlintest:kotlintest-runner-junit4:3.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.5") {
        exclude(group = "org.jetbrains.kotlin")
    }

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")

    testImplementation("io.kotest.extensions:kotest-extensions-pitest:1.1.0")
    testImplementation("io.mockk:mockk:1.12.3")
    testImplementation("io.mockk:mockk-agent-api:1.12.3")
    testImplementation("io.mockk:mockk-agent-jvm:1.12.3")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.6.20")
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
    intellijEngineVersion.set("1.0.656")
    jacocoEngineVersion.set("0.8.7")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xcontext-receivers")
    }
}
