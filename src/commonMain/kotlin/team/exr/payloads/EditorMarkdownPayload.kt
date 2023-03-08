package team.exr.payloads

import kotlinx.serialization.Serializable

@Serializable
data class EditorMarkdownPayload(
    val markdown: String
)
