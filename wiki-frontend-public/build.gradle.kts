plugins {
    `wiki-frontend`
    id("org.jetbrains.compose")
}

repositories {

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
