#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.25.0")
@file:Import("Common.main.kts")

import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
import it.krzeminski.githubactions.domain.RunnerType.MacOSLatest
import it.krzeminski.githubactions.actions.reactivecircus.AndroidEmulatorRunnerV2
import it.krzeminski.githubactions.actions.reactivecircus.AndroidEmulatorRunnerV2.Arch.Arm64V8a
import it.krzeminski.githubactions.domain.triggers.PullRequest
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile
import it.krzeminski.githubactions.dsl.expressions.expr
import java.nio.file.Paths

private val testNameList = listOf(
    "SampleDensityTest",
    "SampleFontSizeTest",
    "SampleLanguageTest",
    "SampleOverdrawTest",
    "SampleParamsTest",
    "SampleMultiApkTest"
)

workflow(
    name = "Build DSL",
    on = listOf(
        PullRequest(),
    ),
    sourceFile = __FILE__.toPath(),
    targetFileName = "build_dsl.yaml",
) {
    val staticAnalysisJob = job(
        id = "static-analysis",
        name = "static-analysis",
        runsOn = UbuntuLatest,
    ) {
        prepareEnvironment()
        runGradleTask("Run Static Analysis", "detekt --stacktrace")
    }

    val unitTestJob = job(
        id = "unit-tests",
        name = "Debug Unit tests",
        runsOn = UbuntuLatest,
        needs = listOf(staticAnalysisJob)
    ) {
        prepareEnvironment()
        runGradleTask("Run Debug Unit Tests", lib.gradle("testDebugUnitTest"))
        publishResults(
            artifactName = "unitTests",
            "lib/build/test-results/**/*.xml",
        )
    }

    job(
        id = "build-dsl",
        name = "Build DSL",
        runsOn = UbuntuLatest,
        needs = listOf(unitTestJob)
    ) {
        prepareEnvironment()
        runGradleTask("Run Debug Unit Tests", lib.gradle("assembleRelease"))
    }

    job(
        id = "test-sample",
        name = "Test samples",
        runsOn = MacOSLatest,
        needs = listOf(staticAnalysisJob),
        _customArguments = mapOf(
            "strategy" to mapOf(
                "fail-fast" to false,
                "matrix" to mapOf(
                    "api-level" to listOf(26, 31),
                )
            )
        ),
    ) {
        prepareEnvironment()
        uses(
            name = "Run tests for \${{ matrix.api-level }}",
            action = AndroidEmulatorRunnerV2(
                _customInputs = mapOf(
                    "api-level" to "\${{ matrix.api-level }}"
                ),
                apiLevel = 0,
                arch = Arm64V8a,
                disableAnimations = true,
                emulatorOptions = "-no-snapshot-save -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim -camera-back none",
                script = "adb devices\n" + testNameList.map { name -> "./gradlew :samples:testDebug --tests \"fi.epicbot.toster.samples.${name}\" --stacktrace" }
                    .joinToString("\n")
            )
        )
        publishResults(
            artifactName = "SampleTestOutput_api_\${{ matrix.api-level }}",
            "lib/build/test-results/**/*.xml",
        )
    }

}.writeToFile()
