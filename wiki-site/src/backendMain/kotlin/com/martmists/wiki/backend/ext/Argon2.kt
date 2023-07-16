package com.martmists.wiki.backend.ext

import com.martmists.wiki.backend.Globals
import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Helper


private const val maxDurationMillis = 100L
private val iterations = Argon2Helper.findIterations(Globals.argon2, maxDurationMillis, 65536, 4)

fun Argon2.hash(password: String): String {
    return hash(iterations, 65536, 4, password.toCharArray())
}

fun Argon2.needsRehash(hash: String): Boolean {
    return needsRehash(hash, iterations, 65536, 4)
}

fun Argon2.verify(hash: String, password: String): Boolean {
    return verify(hash, password.toCharArray())
}
