package team.exr.markdown.providers

import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.html.ImageGeneratingProvider
import org.intellij.markdown.html.URI
import org.intellij.markdown.parser.LinkMap

class ImageTagProvider(linkMap: LinkMap, baseURI: URI?) : ImageGeneratingProvider(linkMap, baseURI) {
    override fun renderLink(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode, info: RenderInfo) {
        val label = info.label.getTextInNode(text)
        val clazz: String
        val realLabel = if (label.startsWith("dark:")) {
            clazz = "img-fluid rounded dark-only"
            label.removePrefix("dark:")
        } else if (label.startsWith("light:")) {
            clazz = "img-fluid rounded light-only"
            label.removePrefix("light:")
        } else {
            clazz = "img-fluid rounded"
            label
        }

        visitor.consumeTagOpen(node, "img",
            "src=\"${makeAbsoluteUrl(info.destination)}\"",
            "alt=\"${getPlainText(realLabel)}\"",
            "class=\"$clazz\"",
            info.title?.let { "title=\"$it\"" },
            autoClose = true)
    }

    private fun getPlainText(text: CharSequence): CharSequence {
        return REGEX.replace(text, "")
    }
}
