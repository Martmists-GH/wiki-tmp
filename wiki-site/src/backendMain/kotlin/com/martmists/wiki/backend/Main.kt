package com.martmists.wiki.backend

import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.schema.execution.Executor
import com.martmists.wiki.backend.database.models.WikiCategory
import com.martmists.wiki.backend.database.models.WikiPage
import com.martmists.wiki.backend.database.tables.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.tomcat.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )

    transaction(database) {
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

    embeddedServer(Tomcat, port = 8080, module = Application::module).start(wait = true)
}

class ExposedGraphQLPatch(prefix: String) : Plugin<Application, Unit, Unit> {
    private val target = "$prefix/graphql".replace("//", "/")

    override val key = AttributeKey<Unit>("ExposedGraphQLPatch")

    override fun install(pipeline: Application, configure: Unit.() -> Unit) {
        pipeline.intercept(ApplicationCallPipeline.Plugins) {
            if (
                call.request.httpMethod == HttpMethod.Post &&
                call.request.path() == target
            ) {
                newSuspendedTransaction {
                    proceed()
                }
            } else {
                proceed()
            }
        }
    }
}

fun Application.module() {
    wiki("/")
}

fun Application.wiki(prefix: String = "/") {
    install(GraphQL) {
        playground = true
        executor = Executor.DataLoaderPrepared

        wrap {
            route(prefix, build=it)
        }

        schema {
            query("hello") {
                resolver { -> "Hello World!" }
            }

            query("wiki") {
                resolver { path: String ->
                    if ("/" in path) {
                        val (category, page) = path.lowercase().split("/")

                        val ctg = WikiCategory.find {
                            WikiCategoryTable.nameUrl eq category
                        }.first()

                        WikiPage.find {
                            WikiPageTable.category eq ctg.id and
                            (WikiPageTable.title eq page)
                        }
                    }
                }
            }
        }
    }

    install(ExposedGraphQLPatch(prefix))
}
