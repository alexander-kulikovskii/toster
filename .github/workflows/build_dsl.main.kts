#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.25.0")
@file:Import("Common.main.kts")

import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
import it.krzeminski.githubactions.domain.triggers.PullRequest
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile
import it.krzeminski.githubactions.dsl.expressions.expr
import java.nio.file.Paths

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
}.writeToFile()
