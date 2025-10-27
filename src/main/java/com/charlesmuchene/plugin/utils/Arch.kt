package com.charlesmuchene.plugin.utils

data class Arch(
    val androidAbi: String,
    val triple: String,
    val swiftArch: String,
    val swiftTarget: String,
    val variantName: String
)

val architectures = mapOf(
    "arm64" to Arch(
        androidAbi = "arm64-v8a",
        triple = "aarch64-linux-android",
        swiftArch = "aarch64",
        swiftTarget = "aarch64-unknown-linux-android",
        variantName = "Arm64"
    ),
    "armv7" to Arch(
        androidAbi = "armeabi-v7a",
        triple = "arm-linux-androideabi",
        swiftArch = "armv7",
        swiftTarget = "armv7-unknown-linux-android",
        variantName = "Armv7"
    ),
    "x86_64" to Arch(
        androidAbi = "x86_64",
        triple = "x86_64-linux-android",
        swiftArch = "x86_64",
        swiftTarget = "x86_64-unknown-linux-android",
        variantName = "X86_64"
    ),
)