package team.exr.config

import com.fasterxml.jackson.annotation.JsonProperty

data class OAuthConfig(
    @JsonProperty("client_id")
    val clientId: String,
    @JsonProperty("client_secret")
    val clientSecret: String,
    @JsonProperty("server_id")
    val serverId: String,
)
