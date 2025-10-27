plugins {
    id("com.gradle.plugin-publish") version "1.3.1"
    kotlin("jvm") version "2.2.21"
    id("java-gradle-plugin")
}

group = "com.charlesmuchene"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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