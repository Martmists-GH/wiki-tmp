package com.martmists.wiki.backend.ktor.plugin

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.martmists.wiki.backend.Globals
import com.martmists.wiki.backend.database.table.AdminTable
import com.martmists.wiki.backend.database.table.MetadataTable
import com.martmists.wiki.backend.ext.hash
import com.martmists.wiki.backend.ext.hex
import com.martmists.wiki.backend.ext.needsRehash
import com.martmists.wiki.backend.ext.spawnTransaction
import com.martmists.wiki.backend.ktor.authentication.principal.AdminPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.coroutines.GlobalScope
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import kotlin.text.toCharArray

fun Application.setupAuthentication() {
    install(Authentication) {
        jwt("jwt") {
            this.realm = Globals.jwtRealm
            verifier(Globals.jwtVerifier)

            validate {
                val id = it.payload.getClaim("id").asInt()
                if (id != null) {
                    AdminPrincipal(id)
                } else {
                    null
                }
            }
        }
    }
}
