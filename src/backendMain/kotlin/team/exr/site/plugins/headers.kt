package team.exr.site.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.forwardedheaders.*
import team.exr.buildconfig.BuildConfig
import team.exr.config.ConfigLoader

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
    }
    if (ConfigLoader.default.website.proxy) {
        install(ForwardedHeaders)
        install(XForwardedHeaders)
    }
}
