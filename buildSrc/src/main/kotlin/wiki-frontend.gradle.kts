import io.miret.etienne.gradle.sass.CompileSass
import org.gradlewebtools.minify.CssMinifyTask

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.miret.etienne.sass")
    id("org.gradlewebtools.minify")
}

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR) {
        moduleName = project.name.removePrefix("wiki-frontend-")

        browser {
            commonWebpackConfig {
                outputFileName = "index-${project.name.removePrefix("wiki-frontend-")}.js"
            }
        }

        binaries.executable()
    }
}

sass {
    version = Versions.sass
    directory = buildDir.resolve(".sass-bin")
}

val bundles by configurations.creating

tasks {
    rootProject.tasks.named("prepareKotlinBuildScriptModel") {
        dependsOn(named("downloadSass"))
    }

    val minifyCss by registering(CssMinifyTask::class) {
        srcDir = projectDir.resolve("src/jsMain/css")
        dstDir = buildDir.resolve("sass")

        options {
            createSourceMaps = !production
            allowUnrecognizedProperties = true
        }
    }

    @Suppress("INACCESSIBLE_TYPE")
    val compileSass by existing(CompileSass::class) {
        outputDir = buildDir.resolve("sass")
        setSourceDir(projectDir.resolve("src/jsMain/sass"))
        loadPath(file("sass-lib"))

        style = if (production) {
            compressed
        } else {
            expanded
        }
        sourceMap = if (production) {
            none
        } else {
            embed
        }
        sourceMapUrls = absolute
    }

    val bundleResources by registering(Sync::class) {
        destinationDir = buildDir.resolve("bundle")

        val frontendTask = getByName("jsBrowser${if (production) "Production" else "Development"}Webpack")

        into("/static/js/") {
            from(frontendTask) {
                include("*.js")

                exclude("skiko.js")
                exclude("*.config.js")
                if (!production) {
                    include("*.map")
                }
            }
        }

        into("/static/css/") {
            from(minifyCss, compileSass)
        }
    }

    artifacts {
        add(bundles.name, bundleResources)
    }

    named("build") {
        dependsOn(bundleResources)
    }
}
