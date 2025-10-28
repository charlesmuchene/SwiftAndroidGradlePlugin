# Swift Android Gradle Plugin

<img src="./media/demo.gif" alt="Demo" width="480"/>

This Gradle plugin simplifies the integration of Swift libraries into Android projects.
Building Swift code for Android requires a lot of boilerplate configuration and scripting.
This plugin encapsulates all that complexity, providing a simple, opinionated way to build and integrate Swift libraries into your Android app.
See a [sample app](./sample).

```kotlin
plugins {
    // apply android app/lib plugin first
    
    id("com.charlesmuchene.swift-android-gradle-plugin") version "0.1.0"
}
```

## Configuration
After applying the plugin, the `swift` config block is available to configure the plugin.
For example, we can set the `apiLevel` and the `abi`s to target when building a debuggable project.

```kotlin
swift {
    apiLevel = 35
    debugAbiFilters = setOf("arm64-v8a", "x86_64")
}
```

See `SAGPConfig` [file](plugin/src/main/java/com/charlesmuchene/plugin/SAGPConfig.kt) for all available configuration options.

## Tasks
The `Swift Android Gradle Plugin` configures several tasks in the `swift` task group to aid in building the _Swift_ code for your project.
These tasks are invoked opaquely for you when you assemble/clean the library or application.

![Tasks](./media/tasks.png)

The tasks in this plugin support building for 3 architectures:
- arm64-v8a
- armeabi-v7a
- x86_64

To add support for additional architectures, see [Arch](./plugin/src/main/java/com/charlesmuchene/plugin/utils/Arch.kt)