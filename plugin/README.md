# Swift Android Gradle Plugin

The Swift Android Gradle Plugin performs the heavy lifting for you when building your app against the _Swift SDK_ for Android.

```kotlin
// build.gradle.kts
plugins {
    // android app/lib plugin must be applied first
    
    id("com.charlesmuchene.swift-android-gradle-plugin") version "0.1.0-alpha"
}

// settings.gradle.kts
pluginManagement {
    repositories {
        // other repos, config
        maven { url = uri("https://jitpack.io") }
    }
}
```
