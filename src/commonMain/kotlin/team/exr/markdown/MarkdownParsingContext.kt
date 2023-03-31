package team.exr.markdown

class MarkdownParsingContext {
    val links: MutableList<String> = mutableListOf()
    val footnotes: MutableMap<Int, String> = mutableMapOf()
}
