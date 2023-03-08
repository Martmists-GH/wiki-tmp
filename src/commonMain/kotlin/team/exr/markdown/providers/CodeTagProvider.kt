package team.exr.markdown.providers

import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.HtmlGenerator

class CodeTagProvider : GeneratingProvider {
    override fun processNode(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
        visitor.consumeHtml("<pre>")
        visitor.consumeTagOpen(node, "code", "class=\"code hljs\"")

        for (child in node.children) {
            if (child.type == MarkdownTokenTypes.CODE_LINE) {
                visitor.consumeHtml(HtmlGenerator.trimIndents(HtmlGenerator.leafText(text, child, false), 4))
            } else if (child.type == MarkdownTokenTypes.EOL) {
                visitor.consumeHtml("\n")
            }
        }

        visitor.consumeHtml("\n")
        visitor.consumeTagClose("code")
        visitor.consumeHtml("</pre>")
    }
}
