package com.martmists.wiki.backend

import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.schema.execution.Executor
import com.martmists.wiki.backend.database.model.WikiCategory
import com.martmists.wiki.backend.database.model.WikiPage
import com.martmists.wiki.backend.database.models.graphql
import com.martmists.wiki.backend.database.table.*
import com.martmists.wiki.backend.ktor.feature.ExposedGraphQLPatch
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.tomcat.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

@Suppress("ExtractKtorModule")
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

    embeddedServer(Tomcat, port = 8080) {



        routing {

        }
    }.start(wait = true)
}
fun Application.wiki() {

}
