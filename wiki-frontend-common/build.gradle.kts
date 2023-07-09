plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
}

repositories {

}

kotlin {
    js(IR) {
        browser()
        binaries.library()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                api(compose.html.core)
                api(compose.html.svg)
            }
        }
    }
}

compose {

}
