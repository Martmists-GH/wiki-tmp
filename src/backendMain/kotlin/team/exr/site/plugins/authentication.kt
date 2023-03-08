package team.exr.site.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import team.exr.config.ConfigLoader
import team.exr.http.httpClient
import team.exr.site.auth.DiscordAuthPrincipal

fun Application.setupAuthentication() {
    val config = ConfigLoader.loadDefault()

    install(Authentication) {
        oauth("discord-oauth") {
            val port = when {
                config.website.proxy || config.website.port == 80 -> {
                    ""
                }
                else -> {
                    ":${config.website.port}"
                }
            }

            urlProvider = { "${config.website.url}$port/auth/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "discord",
                    authorizeUrl = "https://discord.com/api/oauth2/authorize",
                    accessTokenUrl = "https://discord.com/api/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = config.oauth.clientId,
                    clientSecret = config.oauth.clientSecret,
                    defaultScopes = listOf("guilds"),
                )
            }
            client = httpClient
        }

        session<DiscordAuthPrincipal>("discord-session") {
            validate { session ->
                if (session.isValid()) session else null
            }
            challenge {
                call.respondRedirect("/auth/login")
            }
        }
    }

    install(Sessions) {
        cookie<DiscordAuthPrincipal>("discord-session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 604800 // 1 week: https://discord.com/developers/docs/topics/oauth2#authorization-code-grant-access-token-response
        }
    }
}
