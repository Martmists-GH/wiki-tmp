package team.exr.markdown

import com.vladsch.flexmark.ast.BlockQuote
import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.ast.HtmlBlock
import com.vladsch.flexmark.ast.Image
import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.html.renderer.NodeRenderer
import com.vladsch.flexmark.html.renderer.NodeRendererFactory
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler
import com.vladsch.flexmark.util.data.DataHolder
import team.exr.markdown.renderer.*

class WikiNodeRenderer : NodeRenderer {
    class Factory : NodeRendererFactory {
        override fun apply(options: DataHolder): NodeRenderer {
            return WikiNodeRenderer()
        }
    }

    override fun getNodeRenderingHandlers(): MutableSet<NodeRenderingHandler<*>> {
        return mutableSetOf(
            NodeRenderingHandler(Link::class.java, LinkRenderer),
            NodeRenderingHandler(Image::class.java, ImageRenderer),
            NodeRenderingHandler(Heading::class.java, HeadingRenderer),
            NodeRenderingHandler(BlockQuote::class.java, BlockQuoteRenderer),
            NodeRenderingHandler(HtmlBlock::class.java, HtmlBlockRenderer),
        )
    }
}
