package team.exr.site.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import team.exr.ext.getTemplate
import team.exr.site.auth.DiscordAuthPrincipal

fun Routing.auth() {
    authenticate("discord-oauth") {
        get("/auth/login") {
            // automatic redirect
        }

        get("/auth/callback") {
            val principal = call.principal<OAuthAccessTokenResponse.OAuth2>()

            if (principal == null) {
                call.respondRedirect("/auth/login_failed")
            } else {
                val session = DiscordAuthPrincipal(principal.accessToken, principal.refreshToken!!)
                if (session.isValid()) {
                    call.sessions.set(session)
                    call.respondRedirect("/auth/login_success")
                } else {
                    call.respondRedirect("/auth/login_failed")
                }
            }
        }
    }

    getTemplate("auth/login_success")
    getTemplate("auth/login_failed")
}
