package team.exr.markdown.providers

import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.HtmlGenerator

internal class HtmlSubstitutionProvider : GeneratingProvider {
    private val subs = mapOf(
        "<details>" to "<details class=\"collapse-panel\">",
        "<summary>" to "<summary class=\"collapse-header\">",
        "</summary>" to "</summary><div class=\"collapse-content\">",
        "</details>" to "</div></details>",
    )

    override fun processNode(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
        for (child in node.children) {
            if (child.type in listOf(MarkdownTokenTypes.EOL, MarkdownTokenTypes.HTML_BLOCK_CONTENT)) {
                var html = child.getTextInNode(text).toString()

                for ((before, after) in subs) {
                    html = html.replace(before, after)
                }

                visitor.consumeHtml(html)
            }
        }
        visitor.consumeHtml("\n")
    }
}
