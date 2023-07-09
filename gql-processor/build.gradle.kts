plugins {
    kotlin("jvm")
    kotlin("kapt")
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    kapt("com.google.auto.service:auto-service:${Versions.autoService}")
    implementation("com.google.auto.service:auto-service-annotations:${Versions.autoService}")

    implementation("com.google.devtools.ksp:symbol-processing-api:${Versions.ksp}")
    implementation(project(":gql-annotations"))
}
