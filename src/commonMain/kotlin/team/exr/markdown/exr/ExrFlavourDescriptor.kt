package team.exr.markdown.exr

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getParentOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.flavours.space.SFMFlavourDescriptor
import org.intellij.markdown.html.*
import org.intellij.markdown.html.entities.EntityConverter
import org.intellij.markdown.lexer.MarkdownLexer
import org.intellij.markdown.parser.LinkMap
import team.exr.markdown.MarkdownParsingContext
import team.exr.markdown.exr.lexer._EFMLexer
import team.exr.markdown.providers.*

class ExrFlavourDescriptor(useSafeLinks: Boolean = true) : SFMFlavourDescriptor(useSafeLinks) {
    override fun createInlinesLexer(): MarkdownLexer {
        return MarkdownLexer(_EFMLexer())
    }

    internal var ctx = MarkdownParsingContext()

    override fun createHtmlGeneratingProviders(linkMap: LinkMap, baseURI: URI?): Map<IElementType, GeneratingProvider> {
        val parent = super.createHtmlGeneratingProviders(linkMap, baseURI).toMutableMap()

        parent.putAll(mapOf(
            MarkdownElementTypes.HTML_BLOCK to HtmlSubstitutionProvider(),
            MarkdownElementTypes.ATX_1 to HeaderTagProvider("h1", ctx),
            MarkdownElementTypes.ATX_2 to HeaderTagProvider("h2", ctx),
            MarkdownElementTypes.ATX_3 to HeaderTagProvider("h3", ctx),
            MarkdownElementTypes.INLINE_LINK to LinkWithIconProvider(baseURI, false),
            MarkdownElementTypes.IMAGE to ImageTagProvider(linkMap, baseURI).makeXssSafe(useSafeLinks),
            MarkdownElementTypes.CODE_BLOCK to CodeTagProvider(),
            MarkdownElementTypes.CODE_FENCE to CodeFenceProvider(),
            MarkdownElementTypes.BLOCK_QUOTE to BlockQuoteAlertProvider(),
            GFMElementTypes.TABLE to TablesTagProvider(),
            MarkdownElementTypes.AUTOLINK to object : GeneratingProvider {
                override fun processNode(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
                    val linkText = node.getTextInNode(text)
                    val linkLabel = EntityConverter.replaceEntities(
                        linkText.subSequence(1, linkText.length - 1),
                        processEntities = true,
                        processEscapes = false
                    )

                    if (linkLabel.startsWith("footnote:")) {
                        val num = ctx.footnotes.size + 1
                        val content = linkLabel.substring(9)
                        ctx.footnotes[num] = content
                        visitor.consumeHtml("<sup><a href=\"#footnote-$num\" id=\"footnote-ref-$num\">[$num]</a></sup>")
                        return
                    }

                    val linkDestination = LinkMap.normalizeDestination(linkText, false).let {
                        if (useSafeLinks) makeXssSafeDestination(it) else it
                    }
                    visitor.consumeTagOpen(node, "a", "href=\"$linkDestination\"")
                    visitor.consumeHtml(linkLabel)
                    visitor.consumeTagClose("a")
                }

            },
        ))

        return parent
    }

}
