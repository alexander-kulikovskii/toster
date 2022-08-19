#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.25.0")
@file:Import("Common.main.kts")

import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile
import it.krzeminski.githubactions.dsl.expressions.expr
import java.nio.file.Paths

workflow(
    name = "Code coverage",
    on = listOf(
        Push(
            branches = listOf("main"),
        ),
    ),
    sourceFile = __FILE__.toPath(),
    targetFileName = "code_coverage.yaml",
) {
    job(
        id = "code-coverage",
        name = "code-coverage",
        runsOn = UbuntuLatest,
    ) {
        prepareEnvironment()
        runGradleTask("Run Coverage task", "koverReport --stacktrace")
        parseResults("codeCoverage")
        sendToTelegramIfFail(
            "${expr { github.actor }} created commit:\n" +// "PR: ${expr { github.eventPush.number }}\n" +
                    "Job: Code coverage\n" +
                    "Repository: ${expr { github.repository }}\n" +
                    "Status: Failure"
        )

    }
}.writeToFile()
