package com.martmists.wiki.backend.ktor.plugin

import com.martmists.wiki.backend.database.table.MetadataTable
import com.martmists.wiki.backend.ext.hex
import io.ktor.server.application.*
import io.ktor.server.auth.*
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
        form("login-form") {
            userParamName = "username"
            passwordParamName = "password"
            challenge {
                call.respondRedirect("/auth/login")
            }
            validate { credentials ->
                newSuspendedTransaction {

                }
            }
        }

//        session<LoginAuthCookie>("session") {
//            validate { session ->
//                session
//            }
//            challenge {
//                call.sessions.set(LoginRedirectCookie(call.request.path()))
//                call.respondRedirect("/auth/login")
//            }
//        }
    }

    install(Sessions) {
        val encryptKeyName = "COOKIE_ENCRYPT_KEY"
        val signKeyName = "COOKIE_SIGN_KEY"

        val (encryptKey, signKey) = transaction {
            val signKey = MetadataTable.select { MetadataTable.key eq signKeyName }.firstOrNull() ?: let {
                val key = KeyGenerator.getInstance("HmacSHA256").apply {
                    init(512, SecureRandom())
                }.generateKey().encoded.hex()

                MetadataTable.insert {
                    it[key] = signKeyName
                    it[value] = key
                }
            }

            val encryptKey = MetadataTable.select { MetadataTable.key eq encryptKeyName }.firstOrNull() ?: let {
                val key = KeyGenerator.getInstance("AES").apply {
                    init(128, SecureRandom())
                }.generateKey().encoded.hex()

                MetadataTable.insert {
                    it[key] = encryptKeyName
                    it[value] = key
                }
            }

            encryptKey to signKey
        }

//        cookie<LoginAuthCookie>("session") {
//            cookie.path = "/"
//            cookie.maxAgeInSeconds = 604800  // 1 week
//            transform(SessionTransportTransformerEncrypt(hex(encryptKey), hex(signKey)))
//        }
//
//        cookie<LoginRedirectCookie>("redirect") {
//            cookie.path = "/"
//            cookie.maxAgeInSeconds = 300
//            transform(SessionTransportTransformerEncrypt(hex(encryptKey), hex(signKey)))
//        }
    }
}
