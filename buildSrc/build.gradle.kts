plugins {
    kotlin("jvm") version "1.8.20"
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.martmists.com/releases")
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.8.20"))
    implementation(kotlin("serialization", "1.8.20"))

    implementation("com.github.ben-manes:gradle-versions-plugin:0.47.0")
    implementation("com.github.gmazzo.buildconfig:plugin:4.1.1")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.8.20-1.0.11")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")

    implementation("gradle.plugin.io.miret.etienne.gradle:sass-gradle-plugin:1.4.2")
    implementation("org.gradle-webtools.minify:gradle-minify-plugin:1.3.2")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.4.1")

    implementation("com.martmists.commons:commons-gradle:1.0.4")
}
