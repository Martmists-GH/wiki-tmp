@file:Suppress("OPT_IN_USAGE")

package com.martmists.wiki.backend

import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.schema.execution.Executor
import com.martmists.wiki.backend.database.table.*
import com.martmists.wiki.backend.ext.*
import com.martmists.wiki.backend.ktor.plugin.setupAuthentication
import com.martmists.wiki.backend.ktor.plugin.setupContent
import com.martmists.wiki.backend.ktor.plugin.setupHeaders
import com.martmists.wiki.backend.ktor.plugin.setupMonitoring
import com.martmists.wiki.common.payload.LoginRequest
import com.martmists.wiki.common.payload.LoginResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.tomcat.*
import kotlinx.coroutines.GlobalScope
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("ExtractKtorModule")
fun main() {
    transaction(Globals.database) {
        SchemaUtils.createMissingTablesAndColumns(
            // Key-Value store
            MetadataTable,

            // Wiki
            WikiCategoryTable,
            WikiPageTable,
            WikiPageScriptTable,
            WikiImageTable,
        )
    }

    Globals.init()

    embeddedServer(Tomcat, port = 8080) {
        setupMonitoring()
        setupHeaders()
        setupAuthentication()
        setupContent()

        routing {
            staticResources("/static", "/static")

            post("/auth/login") {
                val login = call.receive<LoginRequest>()

                newSuspendedTransaction {
                    if (AdminTable.selectAll().count() == 0L) {
                        val id = AdminTable.insertAndGetId {
                            it[username] = login.username
                            it[password] = Globals.argon2.hash(login.password)
                        }

                        call.respond(LoginResponse(Globals.createJwt(id.value.toString())))
                    } else {
                        AdminTable.select { AdminTable.username eq login.username }.firstOrNull()?.let {
                            if (Globals.argon2.verify(it[AdminTable.password], login.password.toCharArray())) {

                                if (Globals.argon2.needsRehash(it[AdminTable.password])) {
                                    GlobalScope.spawnTransaction {
                                        AdminTable.update({ AdminTable.username eq login.username }) { row ->
                                            row[password] = Globals.argon2.hash(login.password)
                                        }
                                    }
                                }

                                call.respond(LoginResponse(Globals.createJwt(it[AdminTable.id].value.toString())))
                            } else {
                                call.respond(HttpStatusCode.Unauthorized)
                            }
                        } ?: call.respond(HttpStatusCode.Unauthorized)
                    }
                }
            }

            get("/{...}") {
                call.respondOutputStream(ContentType.Text.Html) {
                    this::class.java.getResourceAsStream("/html/index.html")!!.copyTo(this)
                }
            }
        }
    }.start(wait = true)
}
