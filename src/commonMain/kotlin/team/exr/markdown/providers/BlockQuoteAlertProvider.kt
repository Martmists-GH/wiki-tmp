package team.exr.markdown.providers

import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.CompositeASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.HtmlGenerator

class BlockQuoteAlertProvider : GeneratingProvider {
    override fun processNode(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
        val content = node.children.firstOrNull { it.type == MarkdownElementTypes.PARAGRAPH }

        if (content == null) {
            visitor.consumeHtml("<blockquote></blockquote>")
        } else {
            val contentItems = content.children.toMutableList()
            var isWarning = false
            var nodes = node.children

            if (contentItems.size >= 1 && contentItems.first().getTextInNode(text) == "!") {
                contentItems.removeFirst()
                isWarning = true
                nodes = listOf(CompositeASTNode(MarkdownElementTypes.PARAGRAPH, contentItems))
            }

            val clazz = "alert alert-${ if (isWarning) "danger" else "primary" }"
            val heading = if (isWarning) "Warning" else "Note"

            visitor.consumeTagOpen(node, "div", "class=\"$clazz\"", "role=\"alert\"")
            visitor.consumeTagOpen(node, "h4", "class=\"alert-heading\"")
            visitor.consumeHtml(heading)
            visitor.consumeTagClose("h4")
            for (c in nodes) {
                visitor.visitNode(c)
            }
            visitor.consumeTagClose("div")
        }
    }
}
