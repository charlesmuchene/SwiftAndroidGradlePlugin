package com.charlesmuchene.plugin

internal const val EXTENSION_NAME = "swiftConfig"

/**
 * Swift Android Gradle Plugin configuration class
 */
open class SAGPConfig(
    val apiLevel: Int = 28, // Default API level
    val debugAbiFilters: Set<String> = setOf("arm64-v8a"),
    val debugExtraBuildFlags: List<String> = emptyList(),
    val releaseAbiFilters: Set<String> = setOf("arm64-v8a", "armeabi-v7a", "x86_64"),
    val releaseExtraBuildFlags: List<String> = emptyList(),
    val swiftlyPath: String? = null, // Optional custom swiftly path
    val swiftSDKPath: String? = null, // Optional custom Swift SDK path
    val swiftVersion: String = "main-snapshot-2025-10-17", // Swift version
    val androidSdkVersion: String = "DEVELOPMENT-SNAPSHOT-2025-10-17-a-android-0.1" // SDK version
)