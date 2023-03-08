package team.exr.markdown.providers

import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.HtmlGenerator

class CodeFenceProvider : GeneratingProvider {
    override fun processNode(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
        val indentBefore = node.getTextInNode(text).commonPrefixWith(" ".repeat(10)).length

        visitor.consumeHtml("<pre>")

        var state = 0

        var childrenToConsider = node.children
        if (childrenToConsider.last().type == MarkdownTokenTypes.CODE_FENCE_END) {
            childrenToConsider = childrenToConsider.subList(0, childrenToConsider.size - 1)
        }

        var lastChildWasContent = false

        val attributes = ArrayList<String>()
        for (child in childrenToConsider) {
            if (state == 1 && child.type in listOf(MarkdownTokenTypes.CODE_FENCE_CONTENT,
                    MarkdownTokenTypes.EOL)) {
                visitor.consumeHtml(HtmlGenerator.trimIndents(HtmlGenerator.leafText(text, child, false), indentBefore))
                lastChildWasContent = child.type == MarkdownTokenTypes.CODE_FENCE_CONTENT
            }
            if (state == 0 && child.type == MarkdownTokenTypes.FENCE_LANG) {
                attributes.add("class=\"code language-${
                    HtmlGenerator.leafText(text, child).toString().trim().split(' ').first()
                }\"")
            }
            if (state == 0 && child.type == MarkdownTokenTypes.EOL) {
                if (attributes.none { it.startsWith("class=\"") }) {
                    attributes.add("class=\"code hljs\"")
                }
                visitor.consumeTagOpen(node, "code", *attributes.toTypedArray())
                state = 1
            }
        }
        if (state == 0) {
            if (attributes.none { it.startsWith("class=\"") }) {
                attributes.add("class=\"code hljs\"")
            }
            visitor.consumeTagOpen(node, "code", *attributes.toTypedArray())
        }
        if (lastChildWasContent) {
            visitor.consumeHtml("\n")
        }
        visitor.consumeHtml("</code></pre>")
    }
}
