package team.exr.payloads

import kotlinx.serialization.Serializable

@Serializable
data class EditorGroupMetadataPayload(
    val priority: Int
)
