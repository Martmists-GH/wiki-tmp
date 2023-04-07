package hljs

import hljs

fun `hljs$patchMarkdown`() {
    val markdown = hljs.getLanguage("markdown")
    val blockquote = markdown.contains[5]
    blockquote.begin = "^>\\!?\\s+"
}
