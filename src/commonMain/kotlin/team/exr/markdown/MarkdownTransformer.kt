package team.exr.markdown

import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.accept
import org.intellij.markdown.ast.acceptChildren
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.ast.visitors.Visitor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import team.exr.markdown.exr.ExrFlavourDescriptor

object MarkdownTransformer {
    private val flavour = ExrFlavourDescriptor()
    private val parser = MarkdownParser(flavour)

    fun handle(input: String, ctx: MarkdownParsingContext? = null) : String {
        flavour.ctx = ctx ?: MarkdownParsingContext()
        val tree = parser.buildMarkdownTreeFromString(input)
//        debug(input, tree)
        return HtmlGenerator(input, tree, flavour).generateHtml()
    }

    private fun debug(source: String, tree: ASTNode) {
        tree.accept(object : Visitor {
            var indent = 0
            val prefix: String
                get() = "| ".repeat(indent)
            override fun visitNode(node: ASTNode) {
                println("${prefix}${node.type} ${node.getTextInNode(source).toString().replace('\n', ' ')}")
                indent++
                node.acceptChildren(this)
                indent--
            }

        })
    }
}
