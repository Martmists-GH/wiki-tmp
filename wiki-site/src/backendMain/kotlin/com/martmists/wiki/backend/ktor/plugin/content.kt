package com.martmists.wiki.backend.ktor.plugin

import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.GraphQLError
import com.apurebase.kgraphql.schema.dsl.operations.QueryDSL
import com.apurebase.kgraphql.schema.execution.Executor
import com.martmists.wiki.backend.database.model.WikiCategory
import com.martmists.wiki.backend.database.model.WikiPage
import com.martmists.wiki.backend.database.model.graphql
import com.martmists.wiki.backend.database.table.WikiCategoryTable
import com.martmists.wiki.backend.database.table.WikiPageTable
import com.martmists.wiki.backend.ext.requireAuthentication
import com.martmists.wiki.backend.ktor.authentication.principal.AdminPrincipal
import com.martmists.wiki.backend.ktor.feature.ExposedGraphQLPatch
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.statuspages.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.and

fun Application.setupContent() {
    install(AutoHeadResponse)

    install(StatusPages) {
        // TODO
    }

    install(ExposedGraphQLPatch)

    install(GraphQL) {
        playground = true

        wrap {
            authenticate("jwt", strategy = AuthenticationStrategy.Optional, build = it)
        }

        schema {
            context { call ->
                call.principal<AdminPrincipal>()?.let(::inject)
            }

            query("pages") {
                resolver { ->
                    WikiPage.all().map(WikiPage::graphql)
                }
            }

            query("page") {
                resolver { path: String ->
                    if ("/" in path) {
                        val (category, page) = path.lowercase().split("/")

                        val ctg = WikiCategory.find {
                            WikiCategoryTable.nameUrl eq category
                        }.first()

                        WikiPage.find {
                            WikiPageTable.category eq ctg.id and
                                    (WikiPageTable.title eq page)
                        }.first().graphql
                    } else {
                        WikiPage.find {
                            WikiPageTable.title eq path and
                                    WikiPageTable.category.isNull()
                        }.first().graphql
                    }
                }
            }

            query("admins") {
                resolver { ->
                    "Admins here"
                }

                requireAuthentication<AdminPrincipal>()
            }
        }
    }
}
