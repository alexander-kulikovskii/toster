buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        val kotlinVersion = "$$KOTLIN_VERSION$$"
        classpath("com.android.tools.build:gradle:$$GRADLE_TOOLS$$")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
