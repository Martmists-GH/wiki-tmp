package com.martmists.wiki.backend.compiler

import korlibs.io.async.delay
import korlibs.time.TimeSpan
import java.net.URI
import java.net.URL
import java.nio.file.*
import kotlin.io.path.createTempDirectory
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.toPath

class TemporaryGradleProject : AutoCloseable {
    private val directory = createTempDirectory()
    private val outputFile = directory.resolve("build/distributions/index.js").toFile()

    init {
        // Copy `resources/compiler` to directory recursively
        val uri = this::class.java.getResource("/compiler/").toURI()
        val dirPath = try {
            Paths.get(uri)
        } catch (e: FileSystemNotFoundException) {
            // If this is thrown, then it means that we are running the JAR directly (example: not from an IDE)
            val env = mutableMapOf<String, String>()
            FileSystems.newFileSystem(uri, env).getPath("/compiler/")
        }

        fun copy(dir: Path, toDir: Path) {
            Files.walk(dir).forEach { path ->
                val targetPath = toDir.resolve(dir.relativize(path).toString())
                if (Files.isDirectory(path)) {
                    Files.createDirectories(targetPath)
                } else {
                    Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }

        copy(dirPath, directory)
    }

    fun addFile(name: String, content: String) {
        val target = directory.resolve(name).toFile()
        target.parentFile.mkdirs()
        target.writeText(content)
    }

    fun addSourceFile(name: String, content: String) {
        addFile("src/frontendMain/kotlin/$name", content)
    }

    suspend fun compile(): String {
        val process = ProcessBuilder("gradle", "build")
            .directory(directory.toFile())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        val error = process.errorStream.bufferedReader().readText()

        while (process.isAlive) {
            delay(TimeSpan(10.0))
        }

        if (process.exitValue() != 0) {
            throw Exception("Gradle failed to build:\n$output\n$error")
        }

        return outputFile.readText()
    }

    override fun close() {
        directory.toFile().deleteRecursively()
    }
}
