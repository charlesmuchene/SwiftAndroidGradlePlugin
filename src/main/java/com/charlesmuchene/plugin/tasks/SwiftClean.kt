package com.charlesmuchene.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

abstract class SwiftClean : DefaultTask() {

    @get:InputFiles
    val directory: DirectoryProperty = project.objects.directoryProperty()

    init {
        group = "swift"
    }

    @TaskAction
    fun clean() {
        val dir = directory.get().asFile
        if (dir.exists()) {
            dir.deleteRecursively()
            logger.info("Deleted Swift build directory at ${dir.absolutePath}")
        }
    }
}
