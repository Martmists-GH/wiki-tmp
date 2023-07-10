package com.martmists.wiki.backend.ktor.feature

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object ExposedGraphQLPatch : Plugin<Application, Unit, Unit> {
    override val key = AttributeKey<Unit>("ExposedGraphQLPatch")

    override fun install(pipeline: Application, configure: Unit.() -> Unit) {
        pipeline.intercept(ApplicationCallPipeline.Plugins) {
            if (
                call.request.httpMethod == HttpMethod.Post &&
                call.request.path() == "/graphql"
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
