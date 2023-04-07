package team.exr

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

val argon2: Argon2 = Argon2Factory.createAdvanced(32, 256)
private const val maxDurationMillis = 100L
private val iterations = Argon2Helper.findIterations(argon2, maxDurationMillis, 65536, 4)
fun Argon2.hash(password: String): String {
    return hash(iterations, 65536, 4, password.toCharArray())
}
fun Argon2.needsRehash(hash: String): Boolean {
    return needsRehash(hash, iterations, 65536, 4)
}
