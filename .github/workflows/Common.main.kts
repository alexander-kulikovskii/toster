#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.25.0")

import it.krzeminski.githubactions.actions.actions.CacheV3
import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.SetupJavaV3
import it.krzeminski.githubactions.actions.actions.SetupJavaV3.Distribution.Adopt
import it.krzeminski.githubactions.actions.actions.UploadArtifactV3
import it.krzeminski.githubactions.actions.appleboy.TelegramActionV0
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.dsl.JobBuilder
import it.krzeminski.githubactions.dsl.expressions.Contexts
import it.krzeminski.githubactions.dsl.expressions.expr

val TELEGRAM_TO by Contexts.secrets
val TELEGRAM_TOKEN by Contexts.secrets
val BADGE_PROJECT_ID by Contexts.secrets
val BADGE_PROJECT_TOKEN by Contexts.secrets

val lib = "lib"
val samples = "samples"

fun JobBuilder.setUpJDK(
    javaVersion: String = "11",
    distribution: SetupJavaV3.Distribution = Adopt,
) {
    uses(
        name = "Set up JDK",
        action = SetupJavaV3(
            javaVersion = javaVersion,
            distribution = distribution,
        )
    )
}

fun JobBuilder.cacheGradle(
) {
    uses(
        name = "Setup Gradle Dependencies Cache",
        action = CacheV3(
            path = listOf("~/.gradle/caches"),
            key = expr { runner.os } +
                    "-gradle-caches-" +
                    expr { hashFiles("'**/*.gradle'", "'**/*.gradle.kts'", "'**/*.toml'") }
        )
    )
    uses(
        name = "Setup Gradle Wrapper Cache",
        action = CacheV3(
            path = listOf("~/.gradle/wrapper"),
            key = expr { runner.os } +
                    "-gradle-wrapper-" +
                    expr { hashFiles("'**/gradle/wrapper/gradle-wrapper.properties'") }
        )
    )
}

fun JobBuilder.prepareEnvironment() {
    uses(CheckoutV3())
    cacheGradle()
    setUpJDK()
}

fun JobBuilder.runGradleTask(name: String, task: String) {
    uses(
        name = name,
        action = GradleBuildActionV2(
            arguments = task,
        )
    )
}

fun JobBuilder.publishResults(artifactName: String, vararg path: String) {
    uses(
        name = "Publish results",
        condition = "always()",
        action = UploadArtifactV3(
            name = artifactName,
            path = path.toList(),
        )
    )
}

fun JobBuilder.sendToTelegram(
    message: String,
    name: String = "Send to telegram",
    condition: String = "always()",
) {
    uses(
        name = name,
        condition = condition,
        action = TelegramActionV0(
            to = expr { TELEGRAM_TO },
            token = expr { TELEGRAM_TOKEN },
            message = message,
        )
    )
}

fun JobBuilder.sendToTelegramIfFail(message: String) {
    sendToTelegram(message, name = "Send message to telegram on fail", condition = "failure()")
}

fun JobBuilder.parseResults(measurement: String) {
    run(name = "Publish results",
        command = "python3 scripts/report_parser.py -i=${expr { BADGE_PROJECT_ID }} -t=${expr { BADGE_PROJECT_TOKEN }} -k=${measurement}")
}

fun String.gradle(command: String): String = "$this:$command"
