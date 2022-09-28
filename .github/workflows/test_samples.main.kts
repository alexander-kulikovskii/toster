#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.25.0")
@file:Import("Common.main.kts")

import it.krzeminski.githubactions.actions.actions.CacheV3
import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
import it.krzeminski.githubactions.actions.reactivecircus.AndroidEmulatorRunnerV2
import it.krzeminski.githubactions.actions.reactivecircus.AndroidEmulatorRunnerV2.Arch.X8664
import it.krzeminski.githubactions.domain.triggers.Push
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
    name = "Test samples",
    on = listOf(
        Push(
            branches = listOf("main"),
        ),
    ),
    sourceFile = __FILE__.toPath(),
    targetFileName = "test_samples.yaml",
) {
    job(
        id = "test-sample",
        name = "Test samples",
        runsOn = UbuntuLatest,
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
        val avdCache = uses(
            name = "AVD cache",
            action = CacheV3(
                path = listOf("~/.android/avd/*", "~/.android/adb*"),
                key = "avd-" + expr { runner.os } + "\${{ matrix.api-level }}"
            )
        )
        uses(
            name = "Create AVD and generate snapshot for caching \${{ matrix.api-level }}",
            condition = "(steps.${avdCache.id}.outputs.cache-hit != 'true')",
            action = AndroidEmulatorRunnerV2(
                _customInputs = mapOf(
                    "api-level" to "\${{ matrix.api-level }}"
                ),
                apiLevel = 0,
                arch = X8664,
                forceAvdCreation = false,
                disableAnimations = false,
                emulatorOptions = "-no-window -gpu swiftshader_indirect -noaudio -no-boot-anim -camera-back none",
                script = "echo \"Generated AVD snapshot for caching.\""
            )
        )
        uses(
            name = "Run tests for \${{ matrix.api-level }}",
            action = AndroidEmulatorRunnerV2(
                _customInputs = mapOf(
                    "api-level" to "\${{ matrix.api-level }}"
                ),
                apiLevel = 0,
                arch = X8664,
                forceAvdCreation = false,
                disableAnimations = true,
                emulatorOptions = "-no-snapshot-save -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim -camera-back none",
                script = "adb devices\n" + testNameList.map { name -> "./gradlew :samples:testDebug --tests \"fi.epicbot.toster.samples.${name}\" --stacktrace" }
                    .joinToString("\n")
            )
        )
        publishResults(
            artifactName = "SampleTestOutput_api_\${{ matrix.api-level }}",
            "samples/build/toster/",
        )
        sendToTelegramIfFail("Job: Test samples \${{ matrix.api-level }}\nRepository: ${expr { github.repository }}\nStatus: Failure")
    }

}.writeToFile()
