package team.exr.markdown.renderer

import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler

object HeadingRenderer : NodeRenderingHandler.CustomNodeRenderer<Heading> {
    override fun render(node: Heading, context: NodeRendererContext, html: HtmlWriter) {
        val id = context.getNodeId(node)
        if (id != null) {
            html.attr("id", id)
        }

        if (context.htmlOptions.sourcePositionParagraphLines) {
            html.srcPos(node.chars).withAttr().tagLine("h" + node.level) {
                html.srcPos(node.text).withAttr().tag("span")
                context.renderChildren(node)
                html.tag("/span")
            }
        } else {
            html.srcPos(node.text).withAttr().tagLine("h" + node.level) {
                context.renderChildren(
                    node
                )

                html.attr("href", "#$id")
                html.attr("class", "ml-5 text-decoration-none")
                html.withAttr().tag("a") {
                    html.text("#")
                }
            }
        }
    }
}
