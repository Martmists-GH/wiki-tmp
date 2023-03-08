package team.exr.site.plugins

import com.martmists.commons.ktor.PebblePatch
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.pebble.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.statuspages.*
import team.exr.buildconfig.BuildConfig
import team.exr.ext.respondTemplate

sealed class StatusException : Exception()
class NotFoundStatusException : StatusException()

fun Application.setupContent() {
    install(AutoHeadResponse)

    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondTemplate(HttpStatusCode.NotFound, "pages/error/404")
        }

        exception<NotFoundStatusException> { call, err ->
            call.respondTemplate(HttpStatusCode.NotFound, "pages/error/404")
        }

        exception<Exception> { call, err ->
            call.respondTemplate(HttpStatusCode.InternalServerError, "pages/error/500", mapOf(
                "error" to err.stackTraceToString()
            ))
        }
    }

    install(if (BuildConfig.IS_DEVELOPMENT) PebblePatch else Pebble) {
        loader(ClasspathLoader().apply {
            // Weird thing in java with resources and classpaths
            // In a jar release they need to start with / to be found
            // In IDE they should be relative to the resource root
            prefix = "templates/"
            suffix = ".peb"
        })
        cacheActive(!BuildConfig.IS_DEVELOPMENT)
    }
}
