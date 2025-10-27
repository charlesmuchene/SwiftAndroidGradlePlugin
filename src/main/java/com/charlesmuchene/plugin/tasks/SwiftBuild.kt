package com.charlesmuchene.plugin.tasks

import com.charlesmuchene.plugin.SAGPConfig
import com.charlesmuchene.plugin.utils.Arch
import com.charlesmuchene.plugin.utils.swiftResPath
import com.charlesmuchene.plugin.utils.swiftlyPath
import org.gradle.api.GradleException
import org.gradle.api.tasks.Exec

abstract class SwiftBuild : Exec() {
    internal fun configure(arch: Arch, debug: Boolean, config: SAGPConfig) {
        val swiftlyExecutable = swiftlyPath(project)
        val resourcesPath = swiftResPath(arch, project)

        // First perform the validation checks
        if (!project.file(swiftlyExecutable).exists() && swiftlyExecutable != "swiftly") {
            throw GradleException(
                "swiftly not found at: $swiftlyExecutable\n" +
                        "Please install swiftly or configure the path in swiftConfig.swiftlyPath"
            )
        }

        // Check if resources directory exists
        if (!project.file(resourcesPath).exists()) {
            logger.lifecycle("Warning: Swift resources directory not found at: $resourcesPath")
            logger.lifecycle("You may need to install the Swift SDK for Android")
        }

        logger.lifecycle(
            "Building Swift for ${arch.variantName} ${if (debug) "Debug" else "Release"}"
        )
        logger.lifecycle("Using swiftly: $swiftlyExecutable")
        val sdkName = "${arch.swiftTarget}${config.apiLevel}"
        logger.lifecycle("Swift SDK: $sdkName")

        // Set up the build configuration
        val swiftDir = project.file(config.sourcePath)
        if (!swiftDir.exists()) {
            throw GradleException(
                "Swift directory not found at: ${swiftDir.absolutePath}\n" +
                        "Please create the directory and add your Swift code, or configure a custom path in swiftConfig { sourcePath = \"path/to/swift/code\" }"
            )
        }

        val swiftVersion = config.swiftVersion
        val defaultArgs =
            listOf(
                "run", "+$swiftVersion", "swift", "build",
                "--swift-sdk", sdkName,
                "-Xswiftc", "-static-stdlib",
                "-Xswiftc", "-resource-dir",
                "-Xswiftc", resourcesPath
            )
        val configurationArgs = listOf("-c", if (debug) "debug" else "release")
        val extraArgs =
            if (debug) config.debugExtraBuildFlags
            else config.releaseExtraBuildFlags
        val arguments = defaultArgs + configurationArgs + extraArgs
        workingDir(swiftDir)
        args(arguments)
        executable(swiftlyExecutable)
    }
}
