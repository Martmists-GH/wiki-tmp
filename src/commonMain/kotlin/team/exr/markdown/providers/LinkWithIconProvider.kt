package team.exr.markdown.providers

import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.html.*
import org.intellij.markdown.parser.LinkMap

class LinkWithIconProvider(private val baseURI: URI?, private val resolveAnchors: Boolean = false) : GeneratingProvider {
    private val regexToIcon = mapOf(
        Regex("^https://github\\.com/") to "fa-brands fa-github",
        Regex("^https://([a-z]+\\.)?wikipedia\\.([a-z]+\\.)/") to "fa-brands fa-wikipedia-w",
        Regex("^https://(youtube\\.com|youtu\\.be)/") to "fa-brands fa-youtube",
        Regex("^https://(drive|docs)\\.google\\.com/") to "fa-brands fa-google-drive",
    )

    override fun processNode(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
        val info = getRenderInfo(text, node)
            ?: return fallbackProvider.processNode(visitor, text, node)
        renderLink(visitor, text, node, info)
    }

    private fun makeAbsoluteUrl(destination : CharSequence) : CharSequence {
        if (!resolveAnchors && destination.startsWith('#')) {
            return destination
        }

        return baseURI?.resolveToStringSafe(destination.toString()) ?: destination
    }

    private fun renderLink(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode, info: RenderInfo) {
        val linkDestination = info.destination
        val destination = makeAbsoluteUrl(linkDestination)

        if (linkDestination.startsWith("/")) {
            visitor.consumeTagOpen(node, "a", "href=\"$destination\"", "class=\"hyperlink\"")
        } else {
            visitor.consumeTagOpen(node, "a", "href=\"$destination\"", "class=\"hyperlink\"", "target=\"_blank\"")
        }

        labelProvider.processNode(visitor, text, info.label)

        for ((pattern, clazz) in regexToIcon) {
            if (pattern.matches(linkDestination)) {
                visitor.consumeTagOpen(node, "sup")
                visitor.consumeTagOpen(node, "i", "class=\"$clazz\"")
                visitor.consumeTagClose("i")
                visitor.consumeTagClose("sup")
            }
        }

        visitor.consumeTagClose("a")
    }

    private fun getRenderInfo(text: String, node: ASTNode): RenderInfo? {
        val label = node.findChildOfType(MarkdownElementTypes.LINK_TEXT)
            ?: return null
        return RenderInfo(
            label,
            node.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)?.getTextInNode(text)?.let {
                LinkMap.normalizeDestination(it, true)
            } ?: "",
            node.findChildOfType(MarkdownElementTypes.LINK_TITLE)?.getTextInNode(text)?.let {
                LinkMap.normalizeTitle(it)
            }
        )
    }

    private data class RenderInfo(val label: ASTNode, val destination: CharSequence, val title: CharSequence?)

    companion object {
        val fallbackProvider = TransparentInlineHolderProvider()
        val labelProvider = TransparentInlineHolderProvider(1, -1)
    }
}
