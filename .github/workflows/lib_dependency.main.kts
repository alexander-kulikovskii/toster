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
    name = "Library dependencies",
    on = listOf(
        Schedule(listOf(
            Cron(dayWeek = "0", hour = "0", minute = "0")
        )),
    ),
    sourceFile = __FILE__.toPath(),
    targetFileName = "lib_dependency.yaml",
) {
    job(
        id = "lib_dependency",
        name = "lib-dependency",
        runsOn = UbuntuLatest,
    ) {
        prepareEnvironment()
        runGradleTask("Run mutation tests", lib.gradle("dependencyUpdates --stacktrace"))
        publishResults("DependencyOutput", "lib/build/dependencyUpdates/report.txt")
        sendToTelegramIfFail("Job: Library dependencies\nRepository: ${expr { github.repository }}\nStatus: Failure")

    }
}.writeToFile()
