plugins {
    kotlin("multiplatform") version "1.8.20"
    id("org.jetbrains.compose") version "0.5.0-build233"
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "index.js"
            }
        }
        binaries.executable()
    }
}

compose {
    val jsMain by getting {
        dependencies {
            // Compose
            implementation(compose.html.core)
            implementation(compose.html.svg)
        }
    }
}
