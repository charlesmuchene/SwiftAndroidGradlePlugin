package com.charlesmuchene.plugin.tasks

import com.charlesmuchene.plugin.SAGPConfig
import com.charlesmuchene.plugin.utils.Arch
import com.charlesmuchene.plugin.utils.swiftResPath
import com.charlesmuchene.plugin.utils.swiftlyPath
import org.gradle.api.GradleException
import org.gradle.api.tasks.Exec
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Swift build outputs cannot be safely cached")
abstract class SwiftBuild : Exec() {

    init {
        group = "swift"
    }

    internal fun configure(arch: Arch, debug: Boolean, config: SAGPConfig) {
        val swiftlyPath = swiftlyPath(project, config)
        val resourcesPath = swiftResPath(arch, project, config)
        val swiftVersion = config.swiftVersion
        val sdkName = "${arch.swiftTarget}${config.apiLevel}"

        val swiftDir = project.file(config.sourcePath)
        if (!swiftDir.exists()) {
            throw GradleException(
                "Swift directory not found at: ${swiftDir.absolutePath}\n" +
                        "Please create the directory and add your Swift code, or configure a custom path in swiftConfig { sourcePath = \"path/to/swift/code\" }"
            )
        }

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

        workingDir(config.sourcePath)
        executable = swiftlyPath
        args = arguments
    }
}
