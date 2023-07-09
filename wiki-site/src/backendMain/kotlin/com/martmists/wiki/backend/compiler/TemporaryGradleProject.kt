package com.martmists.wiki.backend.compiler

import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText

class TemporaryGradleProject : AutoCloseable {
    val directory = createTempDirectory()
    val buildFile = directory.resolve("build.gradle.kts").toFile()
    val outputFile = directory.resolve("build/productionExecutable/index.js").toFile()

    init {
        this::class.java.getResourceAsStream("/compiler/build.gradle.kts")!!.use {
            buildFile.outputStream().use { out ->
                it.copyTo(out)
            }
        }
    }

    fun addFile(name: String, content: String) {
        directory.resolve(name).toFile().writeText(content)
    }

    fun addSourceFile(name: String, content: String) {
        addFile("src/main/kotlin/$name", content)
    }

    fun compile(): String {
        val process = ProcessBuilder("gradle", "build")
            .directory(directory.toFile())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        val error = process.errorStream.bufferedReader().readText()

        process.waitFor()

        if (process.exitValue() != 0) {
            throw Exception("Gradle failed to build:\n$output\n$error")
        }

        return outputFile.readText()
    }

    override fun close() {
        directory.toFile().deleteRecursively()
    }
}
