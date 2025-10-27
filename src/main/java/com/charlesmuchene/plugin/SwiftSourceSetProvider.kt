package com.charlesmuchene.plugin

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import javax.inject.Inject

open class SwiftSourceSetProvider @Inject constructor() {
    fun configureSourceSet(project: Project) {
        project.extensions.findByType(CommonExtension::class.java)?.apply {
            sourceSets.configureEach { sourceSet ->
                // Add Swift source directory
                sourceSet.java.srcDirs("src/${sourceSet.name}/swift")
            }
        }
    }
}
