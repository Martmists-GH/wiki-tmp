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

object HtmlBlockRenderer : NodeRenderingHandler.CustomNodeRenderer<HtmlBlock> {
    private val subs = mapOf(
        "<details>" to "<details class=\"collapse-panel\">",
        "<summary>" to "<summary class=\"collapse-header\">",
        "</summary>" to "</summary><div class=\"collapse-content\">",
        "</details>" to "</div></details>",
    )

    override fun render(node: HtmlBlock, context: NodeRendererContext, html: HtmlWriter) {
        var content = node.chars.toString()

        for ((from, to) in subs) {
            content = content.replace(from, to)
        }

        node.chars = BasedSequence.of(content)

        html.raw(content)
    }
}
