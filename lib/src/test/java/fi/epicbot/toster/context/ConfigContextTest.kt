package fi.epicbot.toster.context

import fi.epicbot.toster.Then
import fi.epicbot.toster.model.Collage
import fi.epicbot.toster.model.Config
import fi.epicbot.toster.model.Density
import fi.epicbot.toster.model.Devices
import fi.epicbot.toster.model.Emulator
import fi.epicbot.toster.model.FontScale
import fi.epicbot.toster.model.Overdraw
import fi.epicbot.toster.model.Permissions
import fi.epicbot.toster.model.Phone
import fi.epicbot.toster.model.ReportConfig
import fi.epicbot.toster.model.ScreenSize
import fi.epicbot.toster.model.ShellLoggerConfig
import fi.epicbot.toster.model.SwipeOffset
import io.kotest.core.spec.style.BehaviorSpec

private class ConfigContextData(
    val name: String,
    val action: ConfigContext.() -> Unit,
    val expectedCollage: Config,
)

private val configList = listOf(
    ConfigContextData(
        "Default config",
        {
        },
        Config(
            applicationName = "",
            applicationPackageName = "",
            apkUrl = "",
            emulatorPath = "",
            fontScale = null,
            checkOverdraw = Overdraw(check = false, threshold = 0.0),
            reportConfig = ReportConfig(enable = true, customReporters = mutableListOf()),
            shellLoggerConfig = ShellLoggerConfig(enable = true, enableTimestamp = true),
            collage = Collage(enabled = false, rows = 0, columns = 0),
            clearDataBeforeEachRun = false,
            devices = Devices(emulators = emptyList(), phones = emptyList()),
            shellsBeforeAllScreens = emptyArray(),
            shellsAfterAllScreens = emptyArray(),
            permissions = Permissions(granted = emptyList(), revoked = emptyList()),
            testTimeoutMillis = 600 * 1000L,
            deleteAndInstallApk = true,
            horizontalSwipeOffset = SwipeOffset.HorizontalSwipeOffset(
                offsetPx = 160,
                offsetFactor = 0.05
            ),
            verticalSwipeOffset = SwipeOffset.VerticalSwipeOffset(
                offsetPx = 220,
                offsetFactor = 0.08
            ),
            useDemoMode = true,
            failFast = true,
            restartAdbServiceBeforeEachDevice = false,
        )
    ),
    ConfigContextData(
        "App name",
        {
            applicationName("test")
        },
        Config(applicationName = "test"),
    ),
    ConfigContextData(
        "applicationPackageName",
        {
            applicationPackageName("test")
        },
        Config(applicationPackageName = "test"),
    ),
    ConfigContextData(
        "permissions",
        {
            permissions {
                grant("perm1")
            }
        },
        Config(permissions = Permissions(granted = listOf("perm1"))),
    ),
    ConfigContextData(
        "emulatorPath",
        {
            emulatorPath("path")
        },
        Config(emulatorPath = "path"),
    ),
    ConfigContextData(
        "apkUrl",
        {
            apkUrl("url")
        },
        Config(apkUrl = "url"),
    ),
    ConfigContextData(
        "checkOverdraw",
        {
            checkOverdraw(Overdraw(check = true, threshold = 42.0))
        },
        Config(checkOverdraw = Overdraw(check = true, threshold = 42.0)),
    ),
    ConfigContextData(
        "report",
        {
            report {
                disable()
            }
        },
        Config(reportConfig = ReportConfig(enable = false)),
    ),
    ConfigContextData(
        "shellLogger",
        {
            shellLogger {
                disable()
                disableTimestamp()
            }
        },
        Config(shellLoggerConfig = ShellLoggerConfig(enable = false, enableTimestamp = false)),
    ),
    ConfigContextData(
        "fontScaleForAll",
        {
            fontScaleForAll(scale = FontScale.LARGE)
        },
        Config(fontScale = FontScale.LARGE),
    ),
    ConfigContextData(
        "runShellBeforeAllScreens",
        {
            runShellsBeforeAllScreens("shell")
        },
        Config(shellsBeforeAllScreens = arrayOf("shell")),
    ),
    ConfigContextData(
        "runShellAfterAllScreens",
        {
            runShellsAfterAllScreens("shell")
        },
        Config(shellsAfterAllScreens = arrayOf("shell")),
    ),
    ConfigContextData(
        "clearDataBeforeEachRun",
        {
            clearDataBeforeEachRun()
        },
        Config(clearDataBeforeEachRun = true),
    ),
    ConfigContextData(
        "devices",
        {
            devices {
                emulator("emulator")
                phones("uuid")
            }
        },
        Config(
            devices = Devices(
                emulators = listOf(Emulator("emulator")),
                phones = listOf(Phone("uuid"))
            )
        ),
    ),
    ConfigContextData(
        "collage",
        {
            collage {
                enable()
                rows(4)
                columns(2)
            }
        },
        Config(collage = Collage(enabled = true, rows = 4, columns = 2)),
    ),
    ConfigContextData(
        "testTimeout",
        {
            testTimeout(42L)
        },
        Config(testTimeoutMillis = 42L),
    ),
    ConfigContextData(
        "doNotDeleteAndInstallApk",
        {
            doNotDeleteAndInstallApk()
        },
        Config(deleteAndInstallApk = false)
    ),
    ConfigContextData(
        "setHorizontalSwipeOffset",
        {
            setHorizontalSwipeOffset(offsetPx = 4, offsetFactor = 2.0)
        },
        Config(horizontalSwipeOffset = SwipeOffset.HorizontalSwipeOffset(4, 2.0))
    ),
    ConfigContextData(
        "setVerticalSwipeOffset",
        {
            setVerticalSwipeOffset(offsetPx = 4, offsetFactor = 2.0)
        },
        Config(verticalSwipeOffset = SwipeOffset.VerticalSwipeOffset(4, 2.0))
    ),
    ConfigContextData(
        "disableDemoMode",
        {
            disableDemoMode()
        },
        Config(useDemoMode = false)
    ),
    ConfigContextData(
        "disableFailFast",
        {
            disableFailFast()
        },
        Config(failFast = false)
    ),
    ConfigContextData(
        "restartAdbServiceBeforeEachDevice",
        {
            restartAdbServiceBeforeEachDevice()
        },
        Config(restartAdbServiceBeforeEachDevice = true)
    ),
    ConfigContextData(
        "set screenDensity",
        {
            setScreenDensity(Density.HDPI)
        },
        Config(globalScreenDensity = Density.HDPI)
    ),
    ConfigContextData(
        "set screenSize",
        {
            setScreenSize(24, 42)
        },
        Config(globalScreenSize = ScreenSize(24, 42))
    ),
)

internal class ConfigContextTest : BehaviorSpec({
    configList.forEach { configData ->
        Given("check ${configData.name}") {
            val configContext = ConfigContext()
            When("Invoke action") {
                configData.action.invoke(configContext)
                val actual = configContext.config
                val expected = configData.expectedCollage
                Then(
                    "applicationName should be ${expected.applicationName}",
                    actual.applicationName,
                    expected.applicationName
                )
                Then(
                    "applicationPackageName should be ${expected.applicationPackageName}",
                    actual.applicationPackageName,
                    expected.applicationPackageName
                )
                Then("apkUrl should be ${expected.apkUrl}", actual.apkUrl, expected.apkUrl)
                Then(
                    "emulatorPath should be ${expected.emulatorPath}",
                    actual.emulatorPath,
                    expected.emulatorPath
                )
                Then(
                    "fontScale should be ${expected.fontScale}",
                    actual.fontScale,
                    expected.fontScale
                )

                Then(
                    "checkOverdraw.check should be ${expected.checkOverdraw}",
                    actual.checkOverdraw.check,
                    expected.checkOverdraw.check
                )
                Then(
                    "checkOverdraw.threshold  should be ${expected.checkOverdraw}",
                    actual.checkOverdraw.threshold,
                    expected.checkOverdraw.threshold
                )

                Then(
                    "reportConfig.enable should be ${expected.reportConfig}",
                    actual.reportConfig.enable,
                    expected.reportConfig.enable
                )
                Then(
                    "reportConfig.customReporters should be ${expected.reportConfig}",
                    actual.reportConfig.customReporters,
                    expected.reportConfig.customReporters
                )

                Then(
                    "shellLoggerConfig.enable should be ${expected.shellLoggerConfig}",
                    actual.shellLoggerConfig.enable,
                    expected.shellLoggerConfig.enable
                )
                Then(
                    "shellLoggerConfig.enableTimestamp should be ${expected.shellLoggerConfig}",
                    actual.shellLoggerConfig.enableTimestamp,
                    expected.shellLoggerConfig.enableTimestamp
                )

                Then(
                    "collage.enabled should be ${expected.collage}",
                    actual.collage.enabled,
                    expected.collage.enabled
                )
                Then(
                    "collage.rows should be ${expected.collage}",
                    actual.collage.rows,
                    expected.collage.rows
                )
                Then(
                    "collage.columns should be ${expected.collage}",
                    actual.collage.columns,
                    expected.collage.columns
                )

                Then(
                    "clearDataBeforeEachRun should be ${expected.clearDataBeforeEachRun}",
                    actual.clearDataBeforeEachRun,
                    expected.clearDataBeforeEachRun
                )

                Then(
                    "devices.emulators should be ${expected.devices}",
                    actual.devices.emulators.joinToString { it.name },
                    expected.devices.emulators.joinToString { it.name }
                )
                Then(
                    "devices.phones should be ${expected.devices}",
                    actual.devices.phones.joinToString { it.uuid },
                    expected.devices.phones.joinToString { it.uuid }
                )

                Then(
                    "shellBeforeAllScreens should be ${expected.shellsBeforeAllScreens.joinToString()}",
                    actual.shellsBeforeAllScreens.joinToString(),
                    expected.shellsBeforeAllScreens.joinToString()
                )
                Then(
                    "shellAfterAllScreens should be ${expected.shellsAfterAllScreens.joinToString()}",
                    actual.shellsAfterAllScreens.joinToString(),
                    expected.shellsAfterAllScreens.joinToString()
                )

                Then(
                    "permissions.granted should be ${expected.permissions.granted}",
                    actual.permissions.granted,
                    expected.permissions.granted
                )
                Then(
                    "permissions.revoked should be ${expected.permissions.revoked}",
                    actual.permissions.revoked,
                    expected.permissions.revoked
                )

                Then(
                    "testTimeoutMillis should be ${expected.testTimeoutMillis}",
                    actual.testTimeoutMillis,
                    expected.testTimeoutMillis
                )
                Then(
                    "deleteAndInstallApk should be ${expected.deleteAndInstallApk}",
                    actual.deleteAndInstallApk,
                    expected.deleteAndInstallApk
                )

                Then(
                    "horizontalSwipeOffset.offsetFactor should be ${expected.horizontalSwipeOffset}",
                    actual.horizontalSwipeOffset.offsetFactor,
                    expected.horizontalSwipeOffset.offsetFactor
                )
                Then(
                    "horizontalSwipeOffset.offsetPx should be ${expected.horizontalSwipeOffset}",
                    actual.horizontalSwipeOffset.offsetPx,
                    expected.horizontalSwipeOffset.offsetPx
                )

                Then(
                    "verticalSwipeOffset.offsetFactor should be ${expected.verticalSwipeOffset}",
                    actual.verticalSwipeOffset.offsetFactor,
                    expected.verticalSwipeOffset.offsetFactor
                )
                Then(
                    "verticalSwipeOffset.offsetPx should be ${expected.verticalSwipeOffset}",
                    actual.verticalSwipeOffset.offsetPx,
                    expected.verticalSwipeOffset.offsetPx
                )

                Then(
                    "useDemoMode should be ${expected.useDemoMode}",
                    actual.useDemoMode,
                    expected.useDemoMode
                )
                Then("failFast should be ${expected.failFast}", actual.failFast, expected.failFast)
                Then(
                    "restartAdbServiceBeforeEachDevice should be ${expected.restartAdbServiceBeforeEachDevice}",
                    actual.restartAdbServiceBeforeEachDevice,
                    expected.restartAdbServiceBeforeEachDevice
                )
                Then(
                    "globalScreenDensity should be ${expected.globalScreenDensity}",
                    actual.globalScreenDensity?.dpi ?: 0,
                    expected.globalScreenDensity?.dpi ?: 0
                )
                Then(
                    "globalScreenSize should be ${expected.globalScreenSize}",
                    "${actual.globalScreenSize?.width ?: 0}x${actual.globalScreenSize?.height ?: 0}",
                    "${expected.globalScreenSize?.width ?: 0}x${expected.globalScreenSize?.height ?: 0}",
                )
            }
        }
    }
})
