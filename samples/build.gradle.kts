@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(coreLibs.plugins.kotlin.application.get().pluginId)
    id(coreLibs.plugins.kotlin.android.get().pluginId)
    id(coreLibs.plugins.kotlin.serialization.get().pluginId)
    alias(coreLibs.plugins.detekt)
}
apply {
    from("${rootDir}/gradle/dependency-updates.gradle")
}

dependencies {
    implementation(androidLibs.androidx.core)
    implementation(androidLibs.appcompat)
    implementation(androidLibs.material)
    implementation(androidLibs.constraintlayout)
    detektPlugins(coreLibs.detekt.formatting)
    testImplementation(project(":lib"))
}

android {
    compileSdkVersion(33)

    defaultConfig {
        minSdkVersion(26)
        targetSdkVersion(33)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/LICENSE-notice.md")
        exclude("META-INF/library_release.kotlin_module")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
