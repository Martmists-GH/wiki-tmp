import com.github.gmazzo.gradle.plugins.generators.BuildConfigKotlinGenerator
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.*
import org.gradle.configurationcache.extensions.capitalized


plugins {
    kotlin("multiplatform")
    id("com.github.gmazzo.buildconfig")
    id("com.github.johnrengelman.shadow")
    id("com.google.devtools.ksp")
    id("org.jetbrains.compose")
    application
}

kotlin {
    jvm("backend") {
        withJava()
    }

    js("frontend", IR) {
        browser {

        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {

        }

        val backendMain by getting {
            kotlin.srcDir(buildDir.resolve("generated/ksp/backend/backendMain/kotlin"))

            dependencies {
                // KTor
                for (module in listOf(
                    // Core
                    "core", "tomcat",

                    // Authentication
                    "auth", "sessions",

                    // Plugins: Headers
                    "auto-head-response", "default-headers", "compression",

                    // Plugins: Content
                    "status-pages",

                    // Plugins: Monitoring
                    "call-logging",
                )) {
                    implementation("io.ktor:ktor-server-$module-jvm:${Versions.ktor}")
                }

                // Exposed
                for (module in listOf(
                    "core",
                    "jdbc",
                    "dao"
                )) {
                    implementation("org.jetbrains.exposed:exposed-$module:${Versions.exposed}")
                }

                // GraphQL
                implementation("com.apurebase:kgraphql:${Versions.kgraphql}")
                implementation("com.apurebase:kgraphql-ktor:${Versions.kgraphql}")

                // Exposed-GraphQL Bindings
                implementation(project(":gql-annotations"))

                // Database drivers
                if (!production) {
                    implementation("com.h2database:h2:2.1.214")
                }
                implementation("org.postgresql:postgresql:42.5.4")

                // Compression
                implementation("com.soywiz.korlibs.korio:korio-jvm:4.0.8")

                // Password hashing
                implementation("de.mkammerer:argon2-jvm:2.11")

                // Markdown
                implementation("com.vladsch.flexmark:flexmark-all:0.64.0")

                // Logging
                implementation("ch.qos.logback:logback-classic:${Versions.logback}")

                // Stub for Compose Plugin
                compileOnly(compose.desktop.currentOs)
            }
        }

        val frontendMain by getting {
            dependencies {
                // Compose
                implementation(compose.html.core)
                implementation(compose.html.svg)
            }
        }
    }
}

dependencies {
    add("kspBackend", project(":gql-processor"))
}

application {
    mainClass = "com.martmists.wiki.backend.MainKt"
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${!production}")
}

buildConfig {
    generator.set(BuildConfigKotlinGenerator())
    packageName.set("${group}.${name.removePrefix("${rootProject.name}-").replace('-', '.')}")
    className.set("${name.removePrefix("${rootProject.name}-").split('-').joinToString("", transform=String::capitalized)}BuildConfig")
    buildConfigField("kotlin.String", "VERSION", "\"$version\"")
    buildConfigField("kotlin.Boolean", "PRODUCTION", "$production")
}

compose.experimental {
    web.application { }
}

tasks {
    rootProject.tasks.named("prepareKotlinBuildScriptModel") {
        dependsOn(
            named("generateBuildConfig"),
            named("kspKotlinBackend")
        )
    }

    val backendProcessResources by existing(Copy::class) {
        val webpackTask = getByName("frontendBrowser${if (production) "Production" else "Development"}Webpack")

        into("static/js") {
            from(webpackTask) {
                include("frontend.js")
                if (!production) {
                    include("frontend.js.map")
                }
            }
        }

        into("compiler/src/main/kotlin/") {
            from(kotlin.sourceSets.getByName("frontendMain").kotlin.sourceDirectories) {
                exclude("index.kt")
            }
        }
    }

    withType<ShadowJar> {
        configurations = listOf(
            project.configurations["runtimeClasspath"],
        )
    }

    withType<JavaExec> {
        workingDir = rootDir.resolve("run").also { it.mkdirs() }
    }
}
