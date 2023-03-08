package team.exr.site.routes

import com.martmists.commons.ktor.ext.respondTemplate
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import team.exr.ext.getResourceAsStream
import team.exr.site.context.WikiTemplate
import team.exr.site.MarkdownIndexer
import team.exr.site.plugins.NotFoundStatusException

fun Routing.wiki() {
    // TODO: Refactor after database has been added

    get("/wiki") {
        call.respondRedirect("/wiki/index")
    }

    get("/wiki/{path...}") {
        var path = call.request.uri

        if (path.endsWith("/")) {
            path += "index"
        }

        val page = try {
            MarkdownIndexer.getPageFor(path)
        } catch (e: IllegalArgumentException) {
            throw NotFoundStatusException()
        }
        val stream = getResourceAsStream(page.filePath)

        MarkdownIndexer.load(stream).let {
            call.respondTemplate("pages/wiki",
                mapOf("request" to WikiTemplate(
                    "https://${call.request.host()}${call.request.port().let { if (it == 80) "" else ":$it" }}",
                    "https://${call.request.host()}${call.request.port().let { if (it == 80) "" else ":$it" }}${call.request.uri}",
                    MarkdownIndexer.getSidebarEntries(),
                    page,
                    it
                ))
            )
        }
    }
}
