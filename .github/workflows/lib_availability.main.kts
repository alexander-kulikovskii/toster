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

private val LAST_LIB_VERSION = "0.3.2"

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
        _customArguments = mapOf(
            "strategy" to mapOf(
                "fail-fast" to false,
                "matrix" to mapOf(
                    "lib-versions" to listOf(LAST_LIB_VERSION, "0.3.1", "0.3.0", "0.2.9", "0.2.8", "0.2.7"),
                    "gradle-plugins" to listOf("7.5.1"),
                    "gradle-tools" to listOf("7.1.3"),
                    "kotlin-version" to listOf("1.7.20"),

                    "include" to listOf(
                        mapOf(
                            "lib-versions" to LAST_LIB_VERSION,
                            "gradle-plugins" to "7.4.2",
                            "gradle-tools" to "7.0.2",
                            "kotlin-version" to "1.6.21",
                        ),
                        mapOf(
                            "lib-versions" to LAST_LIB_VERSION,
                            "gradle-plugins" to "7.0.2",
                            "gradle-tools" to "7.0.2",
                            "kotlin-version" to "1.5.31",
                        ),
                        mapOf(
                            "lib-versions" to LAST_LIB_VERSION,
                            "gradle-plugins" to "6.2.2",
                            "gradle-tools" to "3.5.3",
                            "kotlin-version" to "1.3.71",
                        ),
                        mapOf(
                            "lib-versions" to LAST_LIB_VERSION,
                            "gradle-plugins" to "6.1.1",
                            "gradle-tools" to "4.0.0",
                            "kotlin-version" to "1.3.72",
                        ),
                    )
                )
            )
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
