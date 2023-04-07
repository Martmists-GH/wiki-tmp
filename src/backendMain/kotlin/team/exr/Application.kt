package team.exr

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.tomcat.*
import team.exr.config.ConfigLoader
import team.exr.database.DatabaseHandler
import team.exr.site.MarkdownIndexer
import team.exr.site.plugins.setupAuthentication
import team.exr.site.plugins.setupContent
import team.exr.site.plugins.setupHeaders
import team.exr.site.plugins.setupMonitoring
import team.exr.site.routes.admin
import team.exr.site.routes.auth
import team.exr.site.routes.wiki

fun main() {
    // Index all markdown pages
    MarkdownIndexer.index()
    DatabaseHandler.load()

    embeddedServer(Tomcat, port = ConfigLoader.default.website.port, host = "0.0.0.0", module = Application::exr).start(wait = true)
}

fun Application.exr() {
    setupMonitoring()
    setupHeaders()
    setupContent()
    setupAuthentication()

    routing {
        static("/static") {
            resources("/static")
        }

//        route("/static") {
//            static("/css") {
//                resources("/static/css")
//            }
//            static("/js") {
//                resources("/static/js")
//            }
//            static("/webfonts") {
//                resources("/static/webfonts")
//            }
//            static("/img") {
//                resources("/static/img")
//
//                // TODO: Assets from database
//            }
//        }

        get("/") {
            call.respondRedirect("/wiki")
        }

        if (developmentMode) {
            get("/reload") {
                MarkdownIndexer.index()
                call.respondRedirect("/")
            }
        }

        auth()
        admin()
        wiki()
    }
}
