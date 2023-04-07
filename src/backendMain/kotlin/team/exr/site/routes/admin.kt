package team.exr.site.routes

import com.martmists.commons.ktor.ext.respondTemplate
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import team.exr.database.DatabaseHandler
import team.exr.database.tables.WikiCategoryTable
import team.exr.database.tables.WikiImageTable
import team.exr.database.tables.WikiPageTable
import team.exr.ext.getTemplate
import team.exr.ext.respondTemplateWithContext
import team.exr.site.context.WikiDatabaseData

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
                    WikiDatabaseData.Page(it[WikiPageTable.id].value, it[WikiPageTable.name], it[WikiPageTable.category].value, it[WikiPageTable.published])
                }
                WikiDatabaseData(groups, pages, images)
            }
            call.respondTemplateWithContext("pages/admin/wiki", mapOf("db" to ctx))
        }

        post("/admin/wiki") {
            // Receive new wiki page contents
            // Store to database
            TODO()
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
