package team.exr.site.auth

import kotlinx.serialization.Serializable

@Serializable
data class PartialGuild(
    val id: String,
    val name: String,
    val icon: String?,
    val owner: Boolean,
    val permissions: String,
    val features: List<String>,
)
