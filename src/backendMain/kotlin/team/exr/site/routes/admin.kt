package team.exr.site.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import team.exr.database.DatabaseHandler
import team.exr.database.tables.WikiCategoryTable
import team.exr.database.tables.WikiImageTable
import team.exr.database.tables.WikiPageTable
import team.exr.ext.getTemplate
import team.exr.ext.respondTemplateWithContext
import team.exr.payloads.EditorGroupMetadataPayload
import team.exr.payloads.EditorMetadataPayload
import team.exr.site.MarkdownIndexer
import team.exr.site.context.WikiDatabaseData
import team.exr.site.context.WikiTemplate
import team.exr.site.plugins.NotFoundStatusException

fun Routing.admin() {
    authenticate("session") {
        get("/admin") {
            call.respondTemplateWithContext("pages/admin/index")
        }


        get("/admin/wiki") {
            val ctx = DatabaseHandler.transactionAsync {
                val images = WikiImageTable.selectAll().map {
                    WikiDatabaseData.Image(it[WikiImageTable.id].value, it[WikiImageTable.name])
                }
                val groups = WikiCategoryTable.selectAll().map {
                    WikiDatabaseData.Group(it[WikiCategoryTable.id].value, it[WikiCategoryTable.name])
                }
                val pages = WikiPageTable.selectAll().map {
                    WikiDatabaseData.Page(
                        it[WikiPageTable.id].value,
                        it[WikiPageTable.name],
                        it[WikiPageTable.category].value
                    )
                }
                WikiDatabaseData(groups, pages, images)
            }
            call.respondTemplateWithContext("pages/admin/wiki", mapOf("db" to ctx))
        }

        get("/admin/wiki/{id}/markdown") {
            val id = call.parameters["id"]?.toInt()

            val page = DatabaseHandler.transactionAsync {
                WikiPageTable.select { WikiPageTable.id eq id }.firstOrNull()?.get(WikiPageTable.content)
            } ?: throw IllegalArgumentException("Invalid ID")

            call.respondText(page)
        }

        get("/admin/wiki/{id}/metadata") {
            val id = call.parameters["id"]?.toInt()

            val payload = DatabaseHandler.transactionAsync {
                val row = WikiPageTable.select { WikiPageTable.id eq id }.firstOrNull() ?: return@transactionAsync null
                EditorMetadataPayload(
                    row[WikiPageTable.description],
                    row[WikiPageTable.published],
                    row[WikiPageTable.priority]
                )
            } ?: throw IllegalArgumentException("Invalid ID")

            call.respond(payload)
        }

        get("/admin/group/{id}/metadata") {
            val id = call.parameters["id"]?.toInt()

            val payload = DatabaseHandler.transactionAsync {
                val row = WikiCategoryTable.select { WikiCategoryTable.id eq id }.firstOrNull() ?: return@transactionAsync null
                EditorGroupMetadataPayload(
                    row[WikiCategoryTable.priority]
                )
            } ?: throw IllegalArgumentException("Invalid ID")

            call.respond(payload)
        }

        get("/admin/wiki/{id}/preview") {
            val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.NotFound)

            val page = MarkdownIndexer.pageFor(id) ?: throw NotFoundStatusException()
            val sidebar = MarkdownIndexer.getSidebarEntries(false)
            val markdown = MarkdownIndexer.render(page)

            call.respondTemplateWithContext("pages/wiki", mapOf(
                "wiki" to WikiTemplate(
                    sidebar,
                    page,
                    markdown
                )
            ))
        }

        post("/admin/wiki") {
            val formData = call.receiveParameters()

            DatabaseHandler.transactionAsync {
                val groupId = formData["group-id"]!!.toInt()
                val groupPriority = formData["group-priority"]!!.toInt()
                val groupName = formData["wiki-group"]!!
                val pageId = formData["wiki-id"]!!.toInt()
                val pageTitle = formData["wiki-title"]!!
                val pageContent = formData["wiki-contents"]!!
                val pageDescription = formData["wiki-description"]!!
                val pagePublished = formData["wiki-published"] == "on"
                val priority = formData["wiki-priority"]!!.toInt()
                val delete = formData["wiki-delete"] == "on"

                val groupResId = if (groupId == 2) {
                    // New group
                    WikiCategoryTable.insertAndGetId {
                        it[WikiCategoryTable.name] = groupName
                        it[WikiCategoryTable.priority] = groupPriority
                    }.value
                } else {
                    // Update group
                    WikiCategoryTable.update({ WikiCategoryTable.id eq groupId }) {
                        it[WikiCategoryTable.name] = groupName
                        it[WikiCategoryTable.priority] = groupPriority
                    }

                    groupId
                }

                if (pageId == -2) {
                    // New entry
                    WikiPageTable.insert {
                        it[WikiPageTable.category] = groupResId
                        it[WikiPageTable.name] = pageTitle
                        it[WikiPageTable.content] = pageContent
                        it[WikiPageTable.description] = pageDescription
                        it[WikiPageTable.published] = pagePublished
                        it[WikiPageTable.priority] = priority
                    }
                } else {
                    if (delete) {
                        WikiPageTable.deleteWhere { WikiPageTable.id eq pageId }
                    } else {
                        // Update entry
                        WikiPageTable.update({ WikiPageTable.id eq pageId }) {
                            it[WikiPageTable.category] = groupResId
                            it[WikiPageTable.name] = pageTitle
                            it[WikiPageTable.content] = pageContent
                            it[WikiPageTable.description] = pageDescription
                            it[WikiPageTable.published] = pagePublished
                            it[WikiPageTable.priority] = priority
                        }
                    }
                }
            }

            GlobalScope.launch {
                MarkdownIndexer.rebuildIndex()
            }

            call.respondRedirect("/admin/wiki")
        }

        post("/admin/image") {
            // Receive new image or new name
            // Store to database

            val form = call.receiveMultipart()
            form.forEachPart {
                when (it) {
                    is PartData.FileItem -> {
                        println(it.originalFileName)
                        println(it.contentType)
                        println(it.streamProvider().readBytes().size)
                    }
                    is PartData.FormItem -> {
                        println(it.name)
                        println(it.value)
                    }
                    else -> {
                        TODO()
                    }
                }
            }
        }

        getTemplate("admin/users")

        post("/admin/users") {
            // Receive new user data
            // Store to database
            TODO()
        }

        getTemplate("admin/branding")

        post("/admin/branding") {
            // Receive new branding data
            // Store to database
            TODO()
        }
    }
}
