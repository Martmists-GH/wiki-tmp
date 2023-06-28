package team.exr.site.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import team.exr.ext.getResourceAsStream
import team.exr.ext.respondTemplateWithContext
import team.exr.site.context.WikiTemplate
import team.exr.site.MarkdownIndexer
import team.exr.site.plugins.NotFoundStatusException

fun Routing.wiki() {
    get("/wiki") {
        call.respondRedirect("/wiki/index")
    }

    get("/wiki/{path...}") {
        var path = call.request.uri

        if (path.endsWith("/")) {
            path += "index"
        }

        val page = MarkdownIndexer.pageFor(path) ?: throw NotFoundStatusException()

        if (!page.public) {
            throw NotFoundStatusException()
        }

         call.respondTemplateWithContext("pages/wiki", mapOf(
            "wiki" to WikiTemplate(
                MarkdownIndexer.getSidebarEntries(true),
                page,
                MarkdownIndexer.render(page)
            )
        ))
    }
}
