package com.martmists.wiki.backend.ktor.plugin

import com.martmists.wiki.main.MainBuildConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.setupHeaders() {
    if (MainBuildConfig.PRODUCTION) {
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
}
