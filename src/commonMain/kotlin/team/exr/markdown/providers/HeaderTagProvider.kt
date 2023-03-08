package team.exr.markdown.providers

import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.HtmlGenerator
import team.exr.ext.toUrlString
import team.exr.markdown.MarkdownParsingContext

class HeaderTagProvider(private val tagName: String, private val ctx: MarkdownParsingContext) : GeneratingProvider {
    override fun processNode(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
        val content = node.children.firstOrNull { it.type == MarkdownTokenTypes.ATX_CONTENT }?.let { it.getTextInNode(text).trim() }

        if (content == null) {
            visitor.consumeTagOpen(node, tagName)
        } else {
            val href = content.toString().toUrlString()
            visitor.consumeTagOpen(node, tagName, "id=\"$href\"")
        }

        for (c in node.children) {
            if (c.type == MarkdownTokenTypes.ATX_CONTENT) {
                val html = c.getTextInNode(text).toString()
                visitor.consumeHtml(html)
            }
        }

        if (content != null) {
            val href = content.toString().toUrlString()
            ctx.links.add(content.toString())
            visitor.consumeTagOpen(node, "a", "href=\"#$href\"", "class=\"ml-5 text-decoration-none\"")
            visitor.consumeHtml("#")
            visitor.consumeTagClose("a")
        }
        visitor.consumeTagClose(tagName)
    }
}
