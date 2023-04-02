package team.exr.markdown.renderer

import com.vladsch.flexmark.ast.BlockQuote
import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.ast.HtmlBlock
import com.vladsch.flexmark.ast.Paragraph
import com.vladsch.flexmark.ast.Text
import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler
import com.vladsch.flexmark.util.sequence.BasedSequence

object BlockQuoteRenderer : NodeRenderingHandler.CustomNodeRenderer<BlockQuote> {
    override fun render(node: BlockQuote, context: NodeRendererContext, html: HtmlWriter) {
        if (node.chars.startsWith(">!")) {
            node.firstChild?.let {
                if (it is Paragraph) {
                    it.firstChild?.let {
                        if (it is Text) {
                            node.openingMarker = BasedSequence.of(">!")
                            it.chars = it.chars.subSequence(1)
                        }
                    }
                }
            }
        }

        html.attr("role", "alert")

        val warning = node.openingMarker.toString() == ">!"

        if (warning) {
            html.attr("class", "alert alert-danger")
        } else {
            html.attr("class", "alert alert-primary")
        }

        html.withAttr().tag("div") {
            html.attr("class", "alert-heading")
            html.withAttr().tag("h4") {
                if (warning) {
                    html.text("Warning")
                } else {
                    html.text("Note")
                }
            }

            context.renderChildren(node)
        }
    }
}
