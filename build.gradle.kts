import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.gmazzo.gradle.plugins.generators.BuildConfigKotlinGenerator
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.martmists.commons.*
import io.miret.etienne.gradle.sass.CompileSass
import org.gradlewebtools.minify.CssMinifyTask
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink
import org.jetbrains.kotlin.gradle.targets.js.ir.SyncExecutableTask
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.10"

    application

    id("com.github.johnrengelman.shadow") version "8.1.0"
    id("com.github.ben-manes.versions") version "0.46.0"
    id("io.miret.etienne.sass") version "1.4.2"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    id("org.gradlewebtools.minify") version "1.3.2"
}

group = "team.exr"
version = "0.0.1"

val production: String? by project
val isDevelopment = production != "true"

repositories {
    mavenCentral()
    martmists()
}
dependencies {
    implementation("io.ktor:ktor-server-forwarded-header-jvm:2.2.4")
    implementation("io.ktor:ktor-server-core-jvm:2.2.4")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:2.2.4")
    implementation("io.micrometer:micrometer-registry-prometheus:1.6.3")
    implementation(kotlin("stdlib-jdk8"))
}

kotlin {
    val idAttr = Attribute.of(String::class.java)
    js("frontendPublic", IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "index.js"
            }
        }
        binaries.executable()
        attributes.attribute(idAttr, "public")
    }
    js("frontendAdmin", IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "index-admin.js"
            }
        }
        binaries.executable()
        attributes.attribute(idAttr, "admin")
    }
    jvm("backend") {
        withJava()
    }

    sourceSets {
        val commonMain by getting {
            buildConfig {
                generator(BuildConfigKotlinGenerator())
                packageName("team.exr.buildconfig")
                buildConfigField("Boolean", "IS_DEVELOPMENT", "$isDevelopment")
            }

            dependencies {
                implementation("io.ktor:ktor-client-core:2.2.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
                implementation("org.jetbrains:markdown:0.3.1")
            }
        }

        val frontendCommonMain by creating {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.529")
            }
        }
        val frontendPublicMain by getting {
            dependsOn(frontendCommonMain)
        }

        val frontendAdminMain by getting {
            dependsOn(frontendCommonMain)

            dependencies {
                implementation("io.ktor:ktor-client-js:2.2.4")
                implementation("io.ktor:ktor-client-content-negotiation-js:2.2.4")
                implementation("io.ktor:ktor-serialization-kotlinx-json-js:2.2.4")
            }
        }

        val backendMain by getting {
            dependencies {
                for (module in listOf(
                    // Core
                    "core", "tomcat",

                    // Authentication for adding new content eventually
                    "auth", "sessions",

                    // Plugins: Headers
                    "auto-head-response", "default-headers", "compression", "forwarded-header",

                    // Plugins: Content
                    "content-negotiation", "status-pages", "html-builder",

                    // Plugins: Monitoring
                    "call-logging"
                )) {
                    implementation("io.ktor:ktor-server-$module-jvm:2.2.4")
                }
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")
                implementation("ch.qos.logback:logback-classic:1.4.5")

                // Http Client
                implementation("io.ktor:ktor-client-cio:2.2.4")
                implementation("io.ktor:ktor-client-content-negotiation-jvm:2.2.4")
                implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.2.4")

                // Config
                implementation("org.yaml:snakeyaml:1.28")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.2")

                // Database libraries
                implementation("org.jetbrains.exposed:exposed-core:0.41.1")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
                implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")

                // Database drivers
                if (isDevelopment) {
                    implementation("com.h2database:h2:2.1.214")
                    implementation("org.xerial:sqlite-jdbc:3.41.0.0")
                }
                implementation("org.postgresql:postgresql:42.5.4")

                // Password hashing
                implementation("de.mkammerer:argon2-jvm:2.11")

                // Markdown
                implementation("com.vladsch.flexmark:flexmark-all:0.64.0")

                // Utilities
                implementation("com.martmists.commons:commons-jvm:1.0.4")
            }
        }
    }
    jvmToolchain(11)
}

application {
    mainClass.set("team.exr.ApplicationKt")

    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

sass {
    version = "1.54.1"
    directory = file ("${rootDir.absolutePath}/.gradle/sass")
}

tasks {
    named("prepareKotlinBuildScriptModel") {
        dependsOn("generateBuildConfig", "downloadSass")
    }

    withType<KotlinJsIrLink> {
        destinationDirectory.set(destinationDirectory.get().dir(name))
    }

    withType<SyncExecutableTask> {
        destinationDir = destinationDir.resolve(displayName)
    }

    // Frontend
    withType<Kotlin2JsCompile> {
        kotlinOptions {
            freeCompilerArgs += listOfNotNull(
                // Disable minimization for debugging
                if (isDevelopment) null else "-Xir-minimized-member-names",
                if (isDevelopment) "-source-map-embed-sources=never" else "-source-map-embed-sources=always",
            )
        }
    }

    // Backend
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += listOfNotNull(
                "-Xcontext-receivers",
                if (isDevelopment) null else "-Xno-call-assertions",
                if (isDevelopment) null else "-Xno-receiver-assertions",
                if (isDevelopment) null else "-Xno-param-assertions",
            )
        }
    }

    withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isStable(currentVersion) && !isStable(candidate.version)
        }
    }

    val minifyPublicCss by creating(CssMinifyTask::class) {
        srcDir = projectDir.resolve("src/frontendPublicMain/css")
        dstDir = buildDir.resolve("sass/public")

        options {
            createSourceMaps = isDevelopment
            allowUnrecognizedProperties = true
        }
    }

    val compilePublicSass by creating(CompileSass::class) {
        outputDir = buildDir.resolve("sass/public")
        setSourceDir(projectDir.resolve("src/frontendPublicMain/sass"))
        loadPath(file("sass-lib"))
        style = compressed
        sourceMap = file
        sourceMapUrls = relative

        dependsOn(minifyPublicCss)
    }

    val minifyAdminCss by creating(CssMinifyTask::class) {
        srcDir = projectDir.resolve("src/frontendAdminMain/css")
        dstDir = buildDir.resolve("sass/admin")

        options {
            createSourceMaps = isDevelopment
            allowUnrecognizedProperties = true
        }
    }

    val compileAdminSass by creating(CompileSass::class) {
        outputDir = buildDir.resolve("sass/admin")
        setSourceDir(projectDir.resolve("src/frontendAdminMain/sass"))
        loadPath(file("sass-lib"))
        style = compressed
        sourceMap = file
        sourceMapUrls = relative

        dependsOn(minifyAdminCss)
    }

    named<Copy>("backendProcessResources") {
        into("/static/js/") {
            from(named("frontendPublicBrowserDistribution")) {
                include("index.js")
                if (isDevelopment) {
                    include("index.js.map")
                }
            }
            from(named("frontendAdminBrowserDistribution")) {
                include("index-admin.js")
                if (isDevelopment) {
                    include("index-admin.js.map")
                }
            }
        }

        into("static/css/") {
            from(compilePublicSass, compileAdminSass)
        }
    }

    named<Jar>("jar") {
        enabled = false
    }

    val shadowJar by named<ShadowJar>("shadowJar") {
        manifest {
            attributes(mapOf(
                "Main-Class" to "team.exr.ApplicationKt"
            ))
        }
    }

    named("build") {
        dependsOn(shadowJar)
    }

    named<JavaExec>("run") {
        dependsOn(shadowJar)
        workingDir(projectDir.resolve("run").also { if (!it.exists()) it.mkdirs() })
    }
}
