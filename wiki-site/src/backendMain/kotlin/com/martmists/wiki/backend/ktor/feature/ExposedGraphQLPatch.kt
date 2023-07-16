package com.martmists.wiki.backend.ktor.feature

import com.martmists.wiki.backend.Globals
import com.martmists.wiki.backend.database.table.MetadataTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import java.lang.IllegalStateException

object ExposedGraphQLPatch : Plugin<Application, Unit, Unit> {
    override val key = AttributeKey<Unit>("ExposedGraphQLPatch")

    override fun install(pipeline: Application, configure: Unit.() -> Unit) {
        pipeline.intercept(ApplicationCallPipeline.Setup) {
            if (
                call.request.httpMethod == HttpMethod.Post &&
                call.request.path() == "/graphql"
            ) {
                newSuspendedTransaction(Dispatchers.IO, Globals.database) {
                    proceed()
                }
            } else {
                proceed()
            }
        }
    }
}
