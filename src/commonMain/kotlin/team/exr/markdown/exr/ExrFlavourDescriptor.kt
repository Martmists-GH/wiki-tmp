package team.exr.markdown.exr

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.flavours.space.SFMFlavourDescriptor
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.URI
import org.intellij.markdown.html.makeXssSafe
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
        ))

        return parent
    }

}
