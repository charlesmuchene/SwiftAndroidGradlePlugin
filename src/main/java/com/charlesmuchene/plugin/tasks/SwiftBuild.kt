package com.charlesmuchene.plugin.tasks

import com.charlesmuchene.plugin.SAGPConfig
import com.charlesmuchene.plugin.utils.Arch
import com.charlesmuchene.plugin.utils.swiftResPath
import com.charlesmuchene.plugin.utils.swiftlyPath
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input

abstract class SwiftBuild : Exec() {

    @get:Input
    abstract val arch: Property<Arch>

    @get:Input
    abstract val debug: Property<Boolean>

    @get:Input
    abstract val config: Property<SAGPConfig>

    // Build the SDK name based on architecture
    private val sdkName by lazy { "${arch.get().swiftTarget}${config.get().apiLevel}" }

    override fun doFirst(action: Action<in Task>): Task {
        val swiftlyPath = swiftlyPath(project)
        val resourcesPath = swiftResPath(arch.get(), project)

        // First perform the validation checks
        if (!project.file(swiftlyPath).exists() && swiftlyPath != "swiftly") {
            throw GradleException(
                "swiftly not found at: $swiftlyPath\n" +
                        "Please install swiftly or configure the path in swiftConfig.swiftlyPath"
            )
        }

        // Check if resources directory exists
        if (!project.file(resourcesPath).exists()) {
            logger.lifecycle("Warning: Swift resources directory not found at: $resourcesPath")
            logger.lifecycle("You may need to install the Swift SDK for Android")
        }

        logger.lifecycle(
            "Building Swift for ${arch.get().variantName} ${if (debug.get()) "Debug" else "Release"}"
        )
        logger.lifecycle("Using swiftly: $swiftlyPath")
        logger.lifecycle("Swift SDK: $sdkName")

        // Set up the build configuration
        val swiftVersion = config.get().swiftVersion
        val defaultArgs =
            listOf(
                "run", "+$swiftVersion", "swift", "build",
                "--swift-sdk", sdkName,
                "-Xswiftc", "-static-stdlib",
                "-Xswiftc", "-resource-dir",
                "-Xswiftc", resourcesPath
            )
        val configurationArgs = listOf("-c", if (debug.get()) "debug" else "release")
        val extraArgs =
            if (debug.get()) config.get().debugExtraBuildFlags
            else config.get().releaseExtraBuildFlags
        val arguments = defaultArgs + configurationArgs + extraArgs

        val swiftDir = project.file(config.get().sourcePath)
        if (!swiftDir.exists()) {
            throw GradleException(
                "Swift directory not found at: ${swiftDir.absolutePath}\n" +
                        "Please create the directory and add your Swift code, or configure a custom path in swiftConfig { sourcePath = \"path/to/swift/code\" }"
            )
        }

        workingDir(swiftDir)
        executable(swiftlyPath)
        args(arguments)

        return super.doFirst(action)
    }
}
