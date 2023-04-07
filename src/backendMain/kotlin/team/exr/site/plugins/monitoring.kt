package team.exr.site.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

fun Application.setupMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        disableDefaultColors()
    }
}
