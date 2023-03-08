package team.exr.ext

import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL

inline fun <reified T : Any> T.getResourceAsStream(path: String): InputStream {
    val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(path)
    val realStream = stream ?: T::class.java.getResourceAsStream(path) ?: throw FileNotFoundException("Resource not found: $path")
    return realStream
}

inline fun <reified T : Any> T.getResource(path: String): URL {
    val url = Thread.currentThread().contextClassLoader.getResource(path)
    val realUrl = url ?: T::class.java.getResource(path) ?: throw FileNotFoundException("Resource not found: $path")
    return realUrl
}
