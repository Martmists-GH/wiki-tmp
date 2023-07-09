import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
//import com.github.gmazzo.gradle.plugins.generators.BuildConfigKotlinGenerator
//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.martmists.commons.*

plugins {
    id("com.github.ben-manes.versions")
}

group = "com.martmists.wiki"
version = "0.0.1"

tasks {
    withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isStable(currentVersion) && !isStable(candidate.version)
        }
    }

    // TODO: This takes too much time, maybe figure out how to cache it?
    named("prepareKotlinBuildScriptModel") {
        dependsOn("dependencyUpdates")
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version
    buildDir = rootProject.buildDir.resolve(name)

    repositories {
        mavenCentral()
        google()
        martmists()
    }
}
