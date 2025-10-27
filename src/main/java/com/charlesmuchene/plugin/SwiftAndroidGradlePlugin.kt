package com.charlesmuchene.plugin

import com.charlesmuchene.plugin.tasks.SwiftBuild
import com.charlesmuchene.plugin.tasks.SwiftCopy
import com.charlesmuchene.plugin.utils.Arch
import com.charlesmuchene.plugin.utils.architectures
import groovy.lang.Closure
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class SwiftAndroidGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val config = project.extensions.create(EXTENSION_NAME, SAGPConfig::class.java)

        // Configure Swift source sets
        val swiftSourceSetProvider = project.objects.newInstance(SwiftSourceSetProvider::class.java)
        project.plugins.withId("com.android.application") {
            swiftSourceSetProvider.configureSourceSet(project)
        }
        project.plugins.withId("com.android.library") {
            swiftSourceSetProvider.configureSourceSet(project)
        }

        project.afterEvaluate {
            val androidExtension = project.extensions.findByName("android")
                ?: throw GradleException("Android extension not found. Make sure to apply this script after the Android plugin.")
            processAppVariants(androidExtension = androidExtension, config = config, project = project)
            processLibVariants(androidExtension = androidExtension, config = config, project = project)
        }
    }


    private fun processAppVariants(androidExtension: Any, config: SAGPConfig, project: Project) {
        try {
            val applicationVariantsMethod = androidExtension::class.java.getMethod("getApplicationVariants")
            val variants = applicationVariantsMethod.invoke(androidExtension)
            val allMethod = variants::class.java.getMethod("all", Closure::class.java)

            allMethod.invoke(variants, object : Closure<Unit>(this) {
                fun doCall(variant: Any) {
                    handleVariant(variant = variant, config = config, project = project)
                }
            })
        } catch (e: NoSuchMethodException) {
            // No applicationVariants found...
        }
    }

    private fun processLibVariants(androidExtension: Any, config: SAGPConfig, project: Project) {
        try {
            val libraryVariantsMethod = androidExtension::class.java.getMethod("getLibraryVariants")
            val variants = libraryVariantsMethod.invoke(androidExtension)
            val allMethod = variants::class.java.getMethod("all", Closure::class.java)

            allMethod.invoke(variants, object : Closure<Unit>(this) {
                fun doCall(variant: Any) {
                    handleVariant(variant = variant, config = config, project = project)
                }
            })
        } catch (e: NoSuchMethodException) {
            // No libraryVariants found
        }
    }
}

private fun handleVariant(variant: Any, config: SAGPConfig, project: Project) {
    val variantClass = variant::class.java

    // Get build type and name using reflection
    val buildTypeMethod = variantClass.getMethod("getBuildType")
    val buildType = buildTypeMethod.invoke(variant)
    val buildTypeClass = buildType::class.java

    val isJniDebuggableMethod = buildTypeClass.getMethod("isJniDebuggable")
    val isDebug = isJniDebuggableMethod.invoke(buildType) as Boolean

    val getNameMethod = variantClass.getMethod("getName")
    val variantName = getNameMethod.invoke(variant) as String

    val getBuildTypeNameMethod = buildTypeClass.getMethod("getName")
    val buildTypeName = getBuildTypeNameMethod.invoke(buildType) as String

    // Get ABI filters
    val abiFilters = getABIFilters(
        config = config,
        isDebug = isDebug,
        buildType = buildType,
        buildTypeClass = buildTypeClass,
    )

    // Create tasks for each architecture
    architectures.values.forEach { arch ->
        if (abiFilters.isEmpty() || abiFilters.contains(arch.androidAbi)) {
            createTasks(
                arch = arch,
                config = config,
                project = project,
                isDebug = isDebug,
                variantName = variantName,
                buildTypeName = buildTypeName,
            )
        }
    }
}

private fun getABIFilters(
    isDebug: Boolean,
    config: SAGPConfig,
    buildTypeClass: Class<out Any>,
    buildType: Any?
): Set<String> = if (isDebug) {
    config.debugAbiFilters
} else {
    config.releaseAbiFilters
}.takeIf { it.isNotEmpty() } ?: try {
    val getNdkMethod = buildTypeClass.getMethod("getNdk")
    val ndk = getNdkMethod.invoke(buildType)
    val getAbiFiltersMethod = ndk::class.java.getMethod("getAbiFilters")
    @Suppress("UNCHECKED_CAST")
    getAbiFiltersMethod.invoke(ndk) as? Set<String> ?: emptySet()
} catch (e: Exception) {
    emptySet()
}

private fun createTasks(
    arch: Arch,
    buildTypeName: String,
    project: Project,
    isDebug: Boolean,
    config: SAGPConfig,
    variantName: String
) {
    val taskName = "${arch.variantName}${buildTypeName.replaceFirstChar { it.uppercaseChar() }}"
    val swiftBuildTask = project.tasks.register("swiftBuild${taskName}", SwiftBuild::class.java) {
        it.debug.set(isDebug)
        it.config.set(config)
        it.arch.set(arch)
    }
    val copyTask = project.tasks.register("copySwift${taskName}", SwiftCopy::class.java) {
        it.debug.set(isDebug)
        it.config.set(config)
        it.arch.set(arch)
        it.dependsOn(swiftBuildTask)
    }

    // Mount to Android build pipeline - try multiple possible task names
    val capitalizedVariantName = variantName.replaceFirstChar { it.uppercaseChar() }
    project.tasks.findByName("merge${capitalizedVariantName}JniLibFolders")?.dependsOn(copyTask)
}