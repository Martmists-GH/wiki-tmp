package team.exr.markdown

import com.vladsch.flexmark.ast.CodeBlock
import com.vladsch.flexmark.ast.FencedCodeBlock
import com.vladsch.flexmark.ext.tables.TableBlock
import com.vladsch.flexmark.html.AttributeProvider
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory
import com.vladsch.flexmark.html.renderer.AttributablePart
import com.vladsch.flexmark.html.renderer.LinkResolverContext
import com.vladsch.flexmark.html.renderer.NodeRenderer
import com.vladsch.flexmark.html.renderer.NodeRendererFactory
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.html.MutableAttributes

class WikiAttributeProvider : AttributeProvider {
    class Factory : IndependentAttributeProviderFactory() {
        override fun apply(context: LinkResolverContext): WikiAttributeProvider {
            return WikiAttributeProvider()
        }
    }

    override fun setAttributes(node: Node, part: AttributablePart, attributes: MutableAttributes) {
        when(node) {
            is TableBlock -> {
                attributes.replaceValue("class", "table table-bordered table-hover")
            }
            is CodeBlock -> {
                val cls = attributes.getValue("class") ?: ""
                attributes.replaceValue("class", "code $cls".trim())
            }
        }
    }
}
