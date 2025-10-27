plugins {
    id("com.gradle.plugin-publish") version "1.2.1"
    kotlin("jvm") version "2.2.21"
    id("java-gradle-plugin")
}

group = "com.charlesmuchene.swift-android-gradle-plugin"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("SwiftAndroidGradlePlugin") {
            id = "com.charlesmuchene.swift-android-gradle-plugin"
            implementationClass = "com.charlesmuchene.plugin.SwiftAndroidGradlePlugin"
        }
    }
}