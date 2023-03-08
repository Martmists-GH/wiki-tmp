package team.exr.site.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import team.exr.ext.getTemplate

fun Routing.download() {
    // Pages
    getTemplate("download/atmosphere")
    getTemplate("download/modmanager")
    getTemplate("download/yuzu")
    getTemplate("download/ryujinx")

    // Files
    get("/download/atmosphere.zip") {
        call.respond(HttpStatusCode.NotFound)
    }

    get("/download/modmanager.zip") {
        call.respond(HttpStatusCode.NotFound)
    }

    get("/download/yuzu.zip") {
        call.respond(HttpStatusCode.NotFound)
    }

    get("/download/ryujinx.zip") {
        call.respond(HttpStatusCode.NotFound)
    }
}
