plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.plugin.publish)
    alias(libs.plugins.java.gradle.plugin)
}

group = "com.charlesmuchene"
version = "0.1.0"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(gradleApi())
    compileOnly(libs.agp.gradle)
    compileOnly(libs.agp.gradle.api)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
}

tasks.test {
    useJUnitPlatform()
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