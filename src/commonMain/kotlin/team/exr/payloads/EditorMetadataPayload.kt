package team.exr.payloads

import kotlinx.serialization.Serializable

@Serializable
data class EditorMetadataPayload(
    val description: String,
    val published: Boolean,
    val priority: Int,
)
