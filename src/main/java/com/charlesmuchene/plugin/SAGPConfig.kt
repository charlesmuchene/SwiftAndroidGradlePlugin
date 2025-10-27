package com.charlesmuchene.plugin

internal const val EXTENSION_NAME = "swift"

/** Swift configuration for Android projects */
open class SAGPConfig(
    var apiLevel: Int = 28, // Default API level
    var debugAbiFilters: Set<String> = setOf("arm64-v8a"),
    var debugExtraBuildFlags: List<String> = emptyList(),
    var releaseAbiFilters: Set<String> = setOf("arm64-v8a", "armeabi-v7a", "x86_64"),
    var releaseExtraBuildFlags: List<String> = emptyList(),
    var swiftlyPath: String? = null, // Optional custom swiftly path
    var swiftSDKPath: String? = null, // Optional custom Swift SDK path
    var sourcePath: String = "src/main/swift", // Path to Swift source code
    var swiftVersion: String = "main-snapshot-2025-10-17", // Swift version
    var androidSdkVersion: String = "DEVELOPMENT-SNAPSHOT-2025-10-17-a-android-0.1" // SDK version
)