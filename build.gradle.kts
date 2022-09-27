buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(coreLibs.plugin.gradle)
        classpath(coreLibs.plugin.kotlin.gradle)
        classpath(coreLibs.plugin.kotlin.serialization)
        classpath(coreLibs.plugin.dexcount)
        classpath(coreLibs.plugin.versions.checker)
        classpath(testLibs.plugin.android.junit5)
        classpath(testLibs.plugin.pitest)
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
