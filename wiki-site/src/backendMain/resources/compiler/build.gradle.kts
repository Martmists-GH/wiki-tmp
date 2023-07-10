plugins {
    kotlin("multiplatform") version "1.8.20"
    id("org.jetbrains.compose") version "1.4.1"
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    js("frontend", IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "index.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val frontendMain by getting {
            dependencies {
                // Compose
                implementation(compose.html.core)
                implementation(compose.html.svg)
                // <DEPENDENCIES>
            }
        }
    }
}

compose.experimental {
    web.application { }
}

tasks {
    val build by existing {
        dependsOn("frontendBrowserProductionWebpack")
    }
}
