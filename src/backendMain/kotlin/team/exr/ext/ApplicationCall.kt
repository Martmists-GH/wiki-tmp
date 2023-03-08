package team.exr.ext

import com.martmists.commons.ktor.ext.locale
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.pebble.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondTemplate(status: HttpStatusCode, template: String, properties: Map<String, Any> = emptyMap()) {
    respond(status, PebbleContent(template, properties, locale = request.locale()))
}
