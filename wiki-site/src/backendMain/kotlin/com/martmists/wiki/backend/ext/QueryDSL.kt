package com.martmists.wiki.backend.ext

import com.apurebase.kgraphql.GraphQLError
import com.apurebase.kgraphql.schema.dsl.operations.QueryDSL
import io.ktor.server.auth.*

inline fun <reified T : Principal> QueryDSL.requireAuthentication() {
    accessRule { ctx ->
        if (ctx.get<T>() != null) {
            null
        } else {
            GraphQLError("This resource requires authentication")
        }
    }
}
