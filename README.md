[![](https://jitpack.io/v/by.epicbot/toster.svg)](https://jitpack.io/#by.epicbot/toster)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/alexander-kulikovskii/toster/actions/workflows/build_dsl.yml/badge.svg)](https://github.com/alexander-kulikovskii/toster/actions/workflows/build_dsl.yml)

# toster

Small test dsl based on adb commands that allows you to test the mobile application close to user actions

### How to install

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
    implementation 'by.epicbot:toster:0.1.1'
}
```
