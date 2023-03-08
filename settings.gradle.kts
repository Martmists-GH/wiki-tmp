buildscript {
    repositories {
        mavenCentral()
        maven("https://maven.martmists.com/releases/")
    }
    dependencies {
        classpath("com.martmists.commons:commons-gradle:1.0.4")
    }
}

rootProject.name = "site"
