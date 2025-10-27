package com.charlesmuchene.plugin.tasks

import com.charlesmuchene.plugin.SAGPConfig
import com.charlesmuchene.plugin.utils.Arch
import com.charlesmuchene.plugin.utils.swiftSDKPath
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Copy

abstract class SwiftCopy : Copy() {

    init {
        group = "swift"
    }

    internal fun configure(arch: Arch, debug: Boolean, config: SAGPConfig) {
        val target = arch.swiftTarget
        val apiLevel = config.apiLevel
        val buildType = if (debug) "debug" else "release"
        val swiftPMBuildPath = "${config.sourcePath}/.build/${target}${apiLevel}/$buildType"

        // Copy c++ shared runtime libraries
        from("${swiftSDKPath(project)}/swift-${config.androidSdkVersion}.artifactbundle/swift-android/ndk-sysroot/usr/lib/${arch.triple}") {
            include("libc++_shared.so")
        }

        // Copy built libraries
        from(project.fileTree(swiftPMBuildPath) {
            include("*.so", "*.so.*")
        })

        into("$ROOT_COPY_DIR/$buildType/jniLibs/${arch.androidAbi}")

        filePermissions {
            it.unix("0644".toInt(8)) // rw-r--r--
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    companion object {
        const val ROOT_COPY_DIR = "src"
    }
}