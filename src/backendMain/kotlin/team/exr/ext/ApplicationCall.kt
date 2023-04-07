package team.exr.ext

import com.martmists.commons.ktor.ext.locale
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.pebble.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import team.exr.site.context.Branding
import team.exr.site.context.WebRequest

suspend fun ApplicationCall.respondTemplateWithContext(template: String, properties: Map<String, Any> = emptyMap()) {
    return respondTemplateWithContext(HttpStatusCode.OK, template, properties)
}

suspend fun ApplicationCall.respondTemplateWithContext(status: HttpStatusCode, template: String, properties: Map<String, Any> = emptyMap()) {
    val defaults = mutableMapOf(
        "request" to WebRequest(
            request.host(),
            "https://${request.host()}${request.port().let { if (it == 80) "" else ":$it" }}",
            "https://${request.host()}${request.port().let { if (it == 80) "" else ":$it" }}${request.uri}"
        ),
        // TODO: Fetch branding from database
        "branding" to Branding(
            "Team EXR",
            listOf(
                Branding.Social("Github", "https://github.com/TeamEXR/", "fa-brands fa-github"),
                Branding.Social("Twitter", "https://twitter.com/TeamEXR_", "fa-brands fa-twitter"),
                Branding.Social("YouTube", "https://www.youtube.com/channel/@teamexr", "fa-brands fa-youtube"),
                Branding.Social("Discord", "https://discord.gg/aNcQM3QeYf", "fa-brands fa-discord"),
            )
        )
    )
    defaults.putAll(properties)
    respond(status, PebbleContent(template, defaults, locale = request.locale()))
}
