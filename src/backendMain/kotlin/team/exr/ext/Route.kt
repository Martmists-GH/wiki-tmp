package team.exr.ext

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.martmists.commons.ktor.ext.respondTemplate

fun Route.getTemplate(path: String) {
    get("/$path") {
        call.respondTemplate("pages/$path", emptyMap())
    }
}
