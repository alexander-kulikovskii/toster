[![](https://jitpack.io/v/by.epicbot/toster.svg)](https://jitpack.io/#by.epicbot/toster)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/alexander-kulikovskii/toster/actions/workflows/build_dsl.yml/badge.svg)](https://github.com/alexander-kulikovskii/toster/actions/workflows/build_dsl.yml)

# toster

Small test dsl based on adb commands that allows you to test the mobile application close to user actions

## How to install

Add next fields in your root `build.gradle` file:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency:
```groovy
dependencies {
    implementation 'by.epicbot:toster:{version}'
}
```

## Usage

To start using Toster DSL you need to create unit tests extended from the `TosterTest` class and configure `Config` and `Screens`.

And just run it as usual unit-test.

From console:
```
./gradlew :project:test
```

Or from Android Studio. Don't forgot to install [Kotest plugin](https://plugins.jetbrains.com/plugin/14080-kotest):


Here's a full list of available functionality written using Toster:
 
```kotlin
class DemoTest : TosterTest(
   Config {
       applicationName("Samples")
       applicationPackageName("fi.epicbot.toster.samples")
       apkUrl("build/outputs/apk/debug/samples-debug.apk")
       permissions {
           grand("PERMISSION")
           grand("ANOTHER_PERMISSION")
       }
       devices {
           emulator("Pixel_3a_API_31_arm64-v8a")
           phone("123456789")
       }
       fontScaleForAll(FontScale.LARGE)
       emulatorPath("android-sdk/emulator")
       clearDataBeforeEachRun()
       runShellBeforeAllScreens("SHELL_COMMAND_BEFORE_ALL")
       runShellAfterAllScreens("SHELL_COMMAND_AFTER_ALL")
   }
   Screens{
       screen{
           name("First screen")
           url("fi.epicbot.toster.samples.SampleFontSizeActivity")
           shortUrl("SampleFontSizeActivity") // optional
           delayAfterOpenMillis(4500L)
           runShellBefore("SOME_COMMAND")
           runShellAfter("SOME_COOMAND")
           clearDataBeforeRun()
           activityParams {
               integer("int_value", 42)
               string("string_value", "42")
               long("long_value", 42L)
           }
           permissions {
               revoke("PERMISSION_1")
               grand("PERMISSION_2")
           }
           fontScale(FontScale.SMALL)
           actions {
               delay(1000L)
               click(x, y)
               swipe(SwipeMove.BottomToTop, delayMillis = 500)
               takeMemoryAllocation()
               takeScreenshot("name")
               runShell("COMMAND")
           }
       }
   }
)
```

## Config

### Application name
Set any name for your project. All output data will be in `build/toster/APPLICATION_NAME`
```kotlin
applicationName("Samples")
```

### Application package name
Package name that you can find in manifest file.

### Apk url

Path to your apk file

### Permissions

You can grant some permission for the application. Toster doesn't install apk with flag `-p` and set them all because for some screens you can revoke them or call clear data.

In the block `Config` you can only grant permissions:

```kotlin
permissions {
   grant("PERMISSION")
   grant("ANOTHER_PERMISSION")
   ...
}
```

Block `Screen` allows grant and revoke permissions:

```kotlin
permissions {
   grant("PERMISSION")
   ...
   revoke("ANOTHER_PERMISSION")
   ...
}
```

### Devices

```kotlin
devices {
   emulator("Pixel_3a_API_31_arm64-v8a")
   ...
   phone("123456789")
   ...
}
```
List of all devices that will be used on testing your app.

Id for the physical devices or emulator which can be find using adb command `adb devices`

Each test at the moment uses one device. No multi test run for now.

Note: You can use `UUID` If you have only one device.

### Font scale for all

You can set up the font scale for the whole screen using the fontScale command.

Available font scales:
- SMALL (0.85)
- DEFAULT (1.0)
- LARGE (1.15)
- LARGEST (1.3)

### Emulator path
Path to emulator in your sdk folder

For example, if my sdk folder

### Clear data before each run

Clear application data before each run. Node that permissions also removed.

### Run shell before all screens

Run shell script before all screens. For example, you can build a special flavor of your app.

### Run shell after all screens

Run shell script after all screens. For example, you can send some analytics to the backend or make work on CI.

## Screen

### Name

Unique name for the screen.

### Url

Full url for your activity including package name. Note, that `aplicationPackageName` can be deference

### Delay after open

Set some timeout before next actions. Because the whole test system doesn't know about some callback, it's very important to set some delays between actions.

### Run shell before

Same as `shellBeforeAllScreens`. It runs some script before this screen. For example, you can prepare some data for this screen.

### Run shell after

Same as above. Runs after this screen.

### Clear data before

Clear application data only before this screen.

###

## Output reports

By default, after all tests you will get a json file with all actions that test done.
It is generated with a default reporter. You can disable it, changing it in block `Config` -> `report`:

```kotlin
Config {
   ...
   report {
       enable(true|false)
   }
   ...
}
```

Or you can even add your own reporter:
```kotlin
Config {
   ...
   report {
       addCustomReporter(MyAwesomeReporter)
   }
   ...
}
```

To implement your own reporter just extends from interface `Reporter`:

```kotlin
 
class MyAwesomeReporter : Reporter {
 
   override fun makeReport(reportOutput: ReportOutput, shellExecutor: ShellExecutor) {
       // Implementation
   }
}
 
```

You will get raw `ReportOutput` with all information about screens and actions, path to all screenshots, memory measurements (if applicable) and `shellExecutor` if it's necessary to run some commands (save report, send somewhere to backend, etc.).
