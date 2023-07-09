plugins {
    `wiki-frontend`
    id("org.jetbrains.compose")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":wiki-frontend-common"))
            }
        }
    }
}

compose {
    experimental {
        web.application {

        }
    }
}
