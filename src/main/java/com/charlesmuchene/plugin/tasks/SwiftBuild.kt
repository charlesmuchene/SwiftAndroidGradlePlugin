package com.charlesmuchene.plugin.tasks

import com.charlesmuchene.plugin.utils.Arch
import com.charlesmuchene.plugin.ext.SwiftConfig
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
    abstract val isDebug: Property<Boolean>

    @get:Input
    abstract val config: Property<SwiftConfig>

    // Build the SDK name based on architecture
    private val sdkName by lazy { "${arch.get().swiftTarget}${config.get().apiLevel}" }

    init {
        val swiftlyPath = swiftlyPath(project)
        val resourcesPath = swiftResPath(arch.get(), project)

        val defaultArgs = listOf(
            "run", "+${config.get().swiftVersion}", "swift", "build",
            "--swift-sdk", sdkName,
            "-Xswiftc", "-static-stdlib",
            "-Xswiftc", "-resource-dir",
            "-Xswiftc", resourcesPath
        )
        val configurationArgs = listOf("-c", if (isDebug.get()) "debug" else "release")
        val extraArgs = if (isDebug.get()) config.get().debugExtraBuildFlags else config.get().releaseExtraBuildFlags
        val arguments = defaultArgs + configurationArgs + extraArgs

        workingDir("src/main/swift")
        executable(swiftlyPath)
        args(arguments)

        workingDir("src/main/swift")
    }

    override fun doFirst(action: Action<in Task>): Task {
        val swiftlyPath = swiftlyPath(project)
        val resourcesPath = swiftResPath(arch.get(), project)
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

        logger.lifecycle("Building Swift for ${arch.get().variantName} ${if (isDebug.get()) "Debug" else "Release"}")
        logger.lifecycle("Using swiftly: $swiftlyPath")
        logger.lifecycle("Swift SDK: $sdkName")
        return super.doFirst(action)
    }
}