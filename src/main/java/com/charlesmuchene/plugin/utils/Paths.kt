package com.charlesmuchene.plugin.utils

import com.charlesmuchene.plugin.SAGPConfig
import org.gradle.api.GradleException
import org.gradle.api.Project

fun swiftSDKPath(project: Project, config: SAGPConfig): String {
    // Return custom path
    config.swiftSDKPath?.let { return it }

    // Try to find Swift SDK in common locations
    val homeDir = System.getProperty("user.home")
    val possiblePaths = listOf(
        "$homeDir/Library/org.swift.swiftpm/swift-sdks/",
        "$homeDir/.config/swiftpm/swift-sdks/",
        "$homeDir/.swiftpm/swift-sdks/",
        "/root/.swiftpm/swift-sdks/"
    )

    for (path in possiblePaths) {
        if (project.file(path).exists()) {
            return path
        }
    }

    throw GradleException("Swift SDK path not found. Please set swiftConfig.swiftSDKPath or install the Swift SDK for Android.")
}


fun swiftlyPath(project: Project, config: SAGPConfig): String {
    // First check custom path
    config.swiftlyPath?.let { return it }

    // Search swiftly in common locations
    val homeDir = System.getProperty("user.home")
    val possiblePaths = listOf(
        "$homeDir/.swiftly/bin/swiftly",
        "$homeDir/.local/share/swiftly/bin/swiftly",
        "$homeDir/.local/bin/swiftly",
        "/usr/local/bin/swiftly",
        "/opt/homebrew/bin/swiftly",
        "/root/.local/share/swiftly/bin/swiftly"
    )

    for (path in possiblePaths) {
        if (project.file(path).exists()) {
            return path
        }
    }

    throw GradleException("Swiftly path not found. Please set swiftConfig.swiftlyPath or install the swiftly.")
}

fun swiftResPath(arch: Arch, project: Project, config: SAGPConfig): String {
    val sdkVersion = config.androidSdkVersion
    val swiftSDKPath = swiftSDKPath(project, config)
    return "$swiftSDKPath/swift-${sdkVersion}.artifactbundle/swift-android/swift-resources/usr/lib/swift_static-${arch.swiftArch}/"
}