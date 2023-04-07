package team.exr.site.context

import team.exr.site.MarkdownIndexer

class WikiTemplate(
    val sidebar: List<MarkdownIndexer.Group>,
    val page: MarkdownIndexer.Page,
    val content: String,
)
