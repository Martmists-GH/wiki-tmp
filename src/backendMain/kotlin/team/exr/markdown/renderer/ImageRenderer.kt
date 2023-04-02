package team.exr.markdown.renderer

import com.vladsch.flexmark.ast.Image
import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler

object ImageRenderer : NodeRenderingHandler.CustomNodeRenderer<Image> {
    override fun render(node: Image, context: NodeRendererContext, html: HtmlWriter) {
        val label = node.text
        val (realLabel, clazz) = when {
            label.startsWith("dark:") -> label.removePrefix("dark:") to "img-fluid rounded hidden-lm"
            label.startsWith("light:") -> label.removePrefix("light:") to "img-fluid rounded hidden-dm"
            else -> label to "img-fluid rounded"
        }
        html.attr("src", node.url)
        html.attr("alt", realLabel)
        html.attr("class", clazz)
        html.withAttr().tag("img", true)
    }
}
