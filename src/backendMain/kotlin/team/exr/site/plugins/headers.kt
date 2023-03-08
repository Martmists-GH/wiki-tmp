package team.exr.site.plugins

import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.application.*
import team.exr.buildconfig.BuildConfig

fun Application.setupHeaders() {
    if (!BuildConfig.IS_DEVELOPMENT) {
        install(Compression) {
            gzip {
                priority = 1.0
            }
            deflate {
                priority = 10.0
                minimumSize(1024)
            }
        }
    }
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
        header("X-Powered-By", "Team EXR")
    }
    install(XForwardedHeaders)
}
