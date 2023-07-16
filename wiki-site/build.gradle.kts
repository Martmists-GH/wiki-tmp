import com.github.gmazzo.gradle.plugins.generators.BuildConfigKotlinGenerator
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.google.common.css.JobDescription
import graphql.GenerateGraphQLTask
import org.gradle.kotlin.dsl.*
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.BaseKotlinCompile
import io.miret.etienne.gradle.sass.CompileSass
import org.gradlewebtools.minify.CssMinifyTask


plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.github.gmazzo.buildconfig")
    id("com.github.johnrengelman.shadow")
    id("com.google.devtools.ksp")
    id("org.jetbrains.compose")
    id("org.gradlewebtools.minify")
    id("io.miret.etienne.sass")
    application
}

kotlin {
    jvm("backend") {
        withJava()
    }

    js("frontend", IR) {
        browser {
            commonWebpackConfig {
                sourceMaps = project.production
                outputFileName = "index.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(buildDir.resolve("generated/graphql/commonMain"))

            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}")

                implementation("io.ktor:ktor-client-core:${Versions.ktor}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")
            }
        }

        val backendMain by getting {
            kotlin.srcDir(buildDir.resolve("generated/ksp/backend/backendMain/kotlin"))

            dependencies {
                // KTor
                for (module in listOf(
                    // Core
                    "core", "tomcat",

                    // Authentication
                    "auth", "auth-jwt",

                    // Plugins: Headers
                    "auto-head-response", "default-headers", "compression",

                    // Plugins: Content
                    "status-pages", "content-negotiation",

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
                    implementation("com.h2database:h2:${Versions.h2}")
                }
                implementation("org.postgresql:postgresql:${Versions.postgres}")

                // Compression
                implementation("com.soywiz.korlibs.korio:korio-jvm:${Versions.korio}")

                // Password hashing
                implementation("de.mkammerer:argon2-jvm:${Versions.argon2}")

                // Markdown
                implementation("com.vladsch.flexmark:flexmark-all:${Versions.flexmark}")

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

                implementation("io.ktor:ktor-client-js:${Versions.ktor}")
                implementation("io.ktor:ktor-client-content-negotiation:${Versions.ktor}")
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
    val createGraphQLBindings by registering(GenerateGraphQLTask::class) {
//        schemaUrl.set("https://localhost:8080/graphql")
        schemaFallback.set(projectDir.resolve("src/commonMain/graphql/schema.graphqls"))
        outputDir.set(buildDir.resolve("generated/graphql/commonMain/"))
        packageName.set("com.martmists.wiki.graphql.client")
    }

    afterEvaluate {
        val kspKotlinBackend by existing {
            dependsOn(createGraphQLBindings)
        }
    }

    rootProject.tasks.named("prepareKotlinBuildScriptModel") {
        dependsOn(
            named("generateBuildConfig"),
            named("kspKotlinBackend"),
        )
    }

    val minifyCss by registering(CssMinifyTask::class) {
        srcDir = projectDir.resolve("src/frontendMain/css")
        dstDir = buildDir.resolve("sass")

        options {
            createSourceMaps = !production
            sourceMapLevel = JobDescription.SourceMapDetailLevel.ALL
            allowUnrecognizedProperties = true
        }

        outputs.upToDateWhen { false }
    }

    val compileSass by existing(CompileSass::class) {
        outputDir = buildDir.resolve("sass")
        setSourceDir(projectDir.resolve("src/frontendMain/scss"))
        loadPath(file("sass-lib"))
        style = compressed
        sourceMap = file
        sourceMapUrls = relative

        outputs.upToDateWhen { false }
        dependsOn(minifyCss)
    }

    val backendProcessResources by existing(Copy::class) {
        val webpackTask = getByName("frontendBrowser${if (project.production) "Production" else "Development"}Webpack")
        webpackTask.outputs.upToDateWhen { false }

        into("static/js") {
            from(webpackTask) {
                include("index.js")
                if (!production) {
                    include("index.js.map")
                }
            }
        }

        into("static/css") {
            from(compileSass) {
                if (production) {
                    exclude("*.map")
                }
            }
        }

        into("compiler/src/main/kotlin/") {
            from(kotlin.sourceSets.getByName("frontendMain").kotlin.sourceDirectories) {
                exclude("index.kt")
            }
        }
    }

    withType<BaseKotlinCompile> {
        dependsOn(createGraphQLBindings)
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
