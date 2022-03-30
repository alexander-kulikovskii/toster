buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        val kotlinVersion = "1.6.10"
        classpath("com.android.tools.build:gradle:7.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath(kotlin("serialization", version = kotlinVersion))
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.6.2.0")
        classpath("pl.droidsonroids.gradle:gradle-pitest-plugin:0.2.8")
        classpath("com.getkeepsafe.dexcount:dexcount-gradle-plugin:3.1.0")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.42.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
