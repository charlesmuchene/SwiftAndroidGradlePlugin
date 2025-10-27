plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.plugin.publish)
    alias(libs.plugins.java.gradle.plugin)
}

group = "com.charlesmuchene"
version = "0.1.0"

dependencies {
    implementation(gradleApi())
    compileOnly(libs.agp.gradle)
    compileOnly(libs.agp.gradle.api)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

gradlePlugin {
    plugins {
        create("swift-android-gradle-plugin") {
            id = "com.charlesmuchene.swift-android-gradle-plugin"
            implementationClass = "com.charlesmuchene.plugin.SwiftAndroidGradlePlugin"
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

gradlePlugin {
    website.set("https://github.com/charlesmuchene/SwiftAndroidGradlePlugin")
    vcsUrl.set("https://github.com/charlesmuchene/SwiftAndroidGradlePlugin.git")
}

publishing {
    repositories {
        mavenLocal()
    }
}