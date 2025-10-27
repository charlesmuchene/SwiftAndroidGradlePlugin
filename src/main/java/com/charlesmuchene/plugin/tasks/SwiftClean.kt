package com.charlesmuchene.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class SwiftClean : DefaultTask() {

    @get:InputDirectory
    @get:Optional
    val swiftBuildDirectory: DirectoryProperty = project.objects.directoryProperty()

    @get:InputDirectory
    @get:Optional
    val jniLibsDirectory: DirectoryProperty = project.objects.directoryProperty().convention(
        // TODO: Expand this to support other variants
        project.layout.projectDirectory.dir("${SwiftCopy.ROOT_COPY_DIR}/debug")
    )

    init {
        description = "Cleans Swift build directories"
        group = "swift"
    }

    @TaskAction
    fun clean() {
        // Delete Swift build directory
        val buildDir = swiftBuildDirectory.get().asFile
        if (buildDir.exists()) {
            buildDir.deleteRecursively()
            logger.info("Deleted Swift build directory at ${buildDir.absolutePath}")
        }

        // Delete copied libs directory if set
        if (jniLibsDirectory.isPresent) {
            val libsDir = jniLibsDirectory.get().asFile
            if (libsDir.exists()) {
                libsDir.deleteRecursively()
                logger.info("Deleted Swift libs directory at ${libsDir.absolutePath}")
            }
        }
    }
}
