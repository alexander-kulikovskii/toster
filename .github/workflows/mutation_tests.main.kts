#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.25.0")
@file:Import("Common.main.kts")

import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
import it.krzeminski.githubactions.domain.triggers.Schedule
import it.krzeminski.githubactions.domain.triggers.Cron
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile
import it.krzeminski.githubactions.dsl.expressions.expr
import java.nio.file.Paths

workflow(
    name = "Mutation tests",
    on = listOf(
        Schedule(listOf(
            Cron(hour = "0", minute = "0")
        )),
    ),
    sourceFile = __FILE__.toPath(),
    targetFileName = "mutation_tests.yaml",
) {
    job(
        id = "mutation-tests",
        name = "mutation-tests",
        runsOn = UbuntuLatest,
    ) {
        prepareEnvironment()
        runGradleTask("Run mutation tests", lib.gradle("pitestDebug --stacktrace"))
        publishResults("MutationTestOutput", "lib/build/reports/pitest/")
        parseResults("pitestCoverage")
        sendToTelegramIfFail("Job: Mutation tests\nRepository: ${expr { github.repository }}\nStatus: Failure")

    }
}.writeToFile()
