package team.exr.site.context

import team.exr.site.MarkdownIndexer

data class WikiTemplate(
    val baseUrl: String,
    val url: String,
    val sidebar: List<MarkdownIndexer.Group>,
    val page: MarkdownIndexer.Page,
    val content: String,
)
