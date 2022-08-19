#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.25.0")
@file:Import("Common.main.kts")

import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.domain.triggers.Schedule
import it.krzeminski.githubactions.domain.triggers.Cron
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile
import it.krzeminski.githubactions.dsl.expressions.expr
import java.nio.file.Paths

workflow(
    name = "Library availability",
    on = listOf(
        Schedule(listOf(
            Cron(hour = "0", minute = "0")
        )),
    ),
    sourceFile = __FILE__.toPath(),
    targetFileName = "lib_availability.yaml",
) {
    job(
        id = "lib_availability",
        name = "lib-availability",
        runsOn = UbuntuLatest,
        strategyMatrix = mapOf(
            "lib-versions" to listOf("0.3.1", "0.3.0", "0.2.9", "0.2.8", "0.2.7"),
            "gradle-plugins" to listOf("7.5.1", "7.4", "7.0.2"),
            "gradle-tools" to listOf("7.1.1", "7.0.2"),
            "kotlin-version" to listOf("1.7.10", "1.6.21", "1.5.31"),
        ),
    ) {
        prepareEnvironment()
        run(name = "Prepare tmp project",
            command = "python3 scripts/check_lib_availability.py -v=\${{ matrix.lib-versions }} -p=\${{ matrix.gradle-plugins }} -t=\${{ matrix.gradle-tools }} -k=\${{ matrix.kotlin-version }}")
        run(name="Download deps",
            command = "sh scripts/check_deps.sh")
        sendToTelegramIfFail(
            "Job: Library availability\n" +
                    "lib-versions: \${{ matrix.lib-versions }}\n" +
                    "gradle-plugins: \${{ matrix.gradle-plugins }}\n" +
                    "gradle-tools: \${{ matrix.gradle-tools }}\n" +
                    "kotlin-version: \${{ matrix.kotlin-version }}\n" +
                    "Repository: ${expr { github.repository }}\n" +
                    "Status: Failure"
        )

    }
}.writeToFile()
