import com.github.gmazzo.gradle.plugins.generators.BuildConfigKotlinGenerator
import com.martmists.commons.martmists
import org.gradle.kotlin.dsl.*
import org.gradle.configurationcache.extensions.capitalized

plugins {
    id("com.github.gmazzo.buildconfig")
}

group = rootProject.group
version = rootProject.version
buildDir = rootProject.buildDir.resolve(name)

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    martmists()
}

buildConfig {
    generator.set(BuildConfigKotlinGenerator())
    packageName.set("${group}.${name.removePrefix("${rootProject.name}-").replace('-', '.')}")
    className.set("${name.removePrefix("${rootProject.name}-").split('-').joinToString("", transform=String::capitalized)}BuildConfig")
    buildConfigField("kotlin.String", "VERSION", "\"$version\"")
    buildConfigField("kotlin.Boolean", "PRODUCTION", "$production")
}

tasks {
    rootProject.tasks.named("prepareKotlinBuildScriptModel") {
        dependsOn(named("generateBuildConfig"))
    }
}
