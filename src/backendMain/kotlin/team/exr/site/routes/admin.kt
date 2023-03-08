package team.exr.site.routes

import com.martmists.commons.ktor.ext.respondTemplate
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import team.exr.ext.getTemplate

fun Routing.admin() {
    authenticate("discord-session") {
        get("/admin") {
            call.respondTemplate("pages/admin/index")
        }

        getTemplate("admin/wiki")

        post("/admin/wiki") {
            // Receive new wiki page contents
            // Store to database
            TODO()
        }
    }
}
