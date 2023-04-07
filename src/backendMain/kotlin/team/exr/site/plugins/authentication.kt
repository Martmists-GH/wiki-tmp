package team.exr.site.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.coroutines.GlobalScope
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import team.exr.database.DatabaseHandler
import team.exr.argon2
import team.exr.database.tables.MetadataTable
import team.exr.database.tables.UserTable
import team.exr.ext.hex
import team.exr.ext.spawnTransaction
import team.exr.hash
import team.exr.needsRehash
import team.exr.site.auth.LoginAuthCookie
import team.exr.site.auth.LoginRedirectCookie
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import kotlin.text.toCharArray

fun Application.setupAuthentication() {
    var createUser = DatabaseHandler.transaction { UserTable.selectAll().count() == 0L }.get()

    install(Authentication) {
        form("login-form") {
            userParamName = "username"
            passwordParamName = "password"
            challenge {
                call.respondRedirect("/auth/login")
            }
            validate { credentials ->
                DatabaseHandler.transactionAsync {
                    if (createUser) {
                        // Treat as initial admin creation
                        val idRef = UserTable.insertAndGetId {
                            it[UserTable.name] = credentials.name
                            it[UserTable.passwordHash] = argon2.hash(credentials.password)
                        }
                        createUser = false
                        LoginAuthCookie(idRef.value)
                    } else {
                        val row = UserTable.select { UserTable.name eq credentials.name }.firstOrNull() ?: return@transactionAsync null
                        if (argon2.verify(row[UserTable.passwordHash], credentials.password.toCharArray())) {
                            // If password needs rehash, rehash in background
                            if (argon2.needsRehash(row[UserTable.passwordHash])) {
                                GlobalScope.spawnTransaction {
                                    UserTable.update({ UserTable.id eq row[UserTable.id] }) {
                                        it[UserTable.passwordHash] = argon2.hash(credentials.password)
                                    }
                                }
                            }

                            LoginAuthCookie(row[UserTable.id].value)
                        } else {
                            null
                        }
                    }
                }
            }
        }

        session<LoginAuthCookie>("session") {
            validate { session ->
                session
            }
            challenge {
                call.sessions.set(LoginRedirectCookie(call.request.path()))
                call.respondRedirect("/auth/login")
            }
        }
    }

    install(Sessions) {
        val encryptKeyName = "COOKIE_ENCRYPT_KEY"
        val signKeyName = "COOKIE_SIGN_KEY"

        val (encryptKey, signKey) = DatabaseHandler.transaction {
            val signKey = MetadataTable.getOrPut(signKeyName) {
                KeyGenerator.getInstance("HmacSHA256").apply {
                    init(512, SecureRandom())
                }.generateKey().encoded.hex()
            }

            val encryptKey = MetadataTable.getOrPut(encryptKeyName) {
                KeyGenerator.getInstance("AES").apply {
                    init(128, SecureRandom())
                }.generateKey().encoded.hex()
            }

            encryptKey to signKey
        }.get()


        cookie<LoginAuthCookie>("session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 604800  // 1 week
            transform(SessionTransportTransformerEncrypt(hex(encryptKey), hex(signKey)))
        }

        cookie<LoginRedirectCookie>("redirect") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 300
            transform(SessionTransportTransformerEncrypt(hex(encryptKey), hex(signKey)))
        }
    }
}
