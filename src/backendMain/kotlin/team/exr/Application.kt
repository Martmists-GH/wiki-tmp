package team.exr

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.tomcat.*
import org.slf4j.event.Level
import team.exr.config.ConfigLoader
import team.exr.site.MarkdownIndexer
import team.exr.site.plugins.setupAuthentication
import team.exr.site.plugins.setupContent
import team.exr.site.plugins.setupHeaders
import team.exr.site.routes.wiki

fun main() {
    // Index all markdown pages
    MarkdownIndexer.index()
    val config = ConfigLoader.loadDefault()

    embeddedServer(Tomcat, port = config.website.port, host = "0.0.0.0", module = Application::exr).start(wait = true)
}

fun Application.exr() {
    install(CallLogging) {
        level = Level.INFO
    }
    setupHeaders()
    setupContent()
    setupAuthentication()

    routing {
        static("/static") {
            resources("/static")
        }

        get("/") {
//                // Optional: Use the DSL to generate parts of HTML in code
//                // Tends to be a little slower than templates if it uses a lot of expressions
//                val sb = StringBuilder()
//                HTMLStreamBuilder(sb, prettyPrint = false, xhtmlCompatible = false).div("content") {
//                    h1 { +"Team EXR" }
//                }
//                call.respondTemplate("pages/index", mapOf("generated" to sb.toString()))
            call.respondRedirect("/wiki")
        }

        if (developmentMode) {
            get("/reload") {
                MarkdownIndexer.index()
                call.respondRedirect("/")
            }
        }


        // Terms of Service
//        getTemplate("terms")
        // Privacy Policy
//        getTemplate("privacy")

//        auth()
//        admin()
//        download()
        wiki()
    }
}
