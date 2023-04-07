package team.exr.site.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import team.exr.ext.getTemplate
import team.exr.ext.respondTemplateWithContext
import team.exr.site.auth.LoginAuthCookie
import team.exr.site.auth.LoginRedirectCookie

fun Routing.auth() {
    get("/auth/login") {
        call.respondTemplateWithContext("pages/auth/login")
    }

    authenticate("login-form") {
        post("/auth/login") {
            val principal = call.principal<LoginAuthCookie>()!!
            call.sessions.set(principal)
            val redirect = call.sessions.get<LoginRedirectCookie>()
            call.sessions.clear<LoginRedirectCookie>()
            call.respondRedirect(redirect?.path ?: "/admin")
        }
    }

    get("/auth/logout") {
        call.sessions.clear<LoginAuthCookie>()
        call.respondRedirect("/")
    }

    getTemplate("auth/login_success")
    getTemplate("auth/login_failed")
}
