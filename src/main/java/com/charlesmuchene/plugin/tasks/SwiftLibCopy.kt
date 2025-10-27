package com.charlesmuchene.plugin.tasks

import com.charlesmuchene.plugin.utils.Arch
import com.charlesmuchene.plugin.SAGPConfig
import com.charlesmuchene.plugin.utils.swiftSDKPath
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input

abstract class SwiftLibCopy : Copy() {
    @get:Input
    abstract val arch: Property<Arch>

    @get:Input
    abstract val isDebug: Property<Boolean>

    @get:Input
    abstract val config: Property<SAGPConfig>

    init {
        val target = arch.get().swiftTarget
        val apiLevel = config.get().apiLevel
        val buildType = if (isDebug.get()) "debug" else "release"
        val swiftPmBuildPath = "src/main/swift/.build/${target}${apiLevel}/$buildType"

        // Copy c++ shared runtime libraries
        from("${swiftSDKPath(project)}/swift-${config.get().androidSdkVersion}.artifactbundle/swift-android/ndk-sysroot/usr/lib/${arch.get().triple}") {
            include("libc++_shared.so")
        }

        // Copy built libraries
        from(project.fileTree(swiftPmBuildPath) {
            include("*.so", "*.so.*")
        })

        into("src/$buildType/jniLibs/${arch.get().androidAbi}")

        filePermissions {
            it.unix("0644".toInt(8)) // rw-r--r--
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}