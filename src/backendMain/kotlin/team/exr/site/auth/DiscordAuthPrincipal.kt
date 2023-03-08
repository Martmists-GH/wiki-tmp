package team.exr.site.auth

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.auth.*
import team.exr.config.ConfigLoader
import team.exr.http.httpClient

data class DiscordAuthPrincipal(val accessToken: String, val refreshToken: String) : Principal {
    suspend fun isValid(): Boolean {
        val config = ConfigLoader.loadDefault()

        val response = httpClient.get("https://discord.com/api/v10/users/@me/guilds") {
            header("Authorization", "Bearer $accessToken")
        }

        val guilds = response.body<List<PartialGuild>>()
        return guilds.any { it.id == config.oauth.serverId }
    }
}
