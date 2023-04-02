package team.exr.markdown.renderer

import com.martmists.commons.logging.logger
import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler

object LinkRenderer : NodeRenderingHandler.CustomNodeRenderer<Link> {
    private val logger by logger()

    private val regexToIcon = mapOf(
        Regex("^https://github\\.com/") to "fa-brands fa-github",
        Regex("^https://(?:[a-z]+\\.)?wikipedia\\.[a-z]+/") to "fa-brands fa-wikipedia-w",
        Regex("^https://youtu(?:be\\.com|\\.be)/") to "fa-brands fa-youtube",
        Regex("^https://(?:drive|docs)\\.google\\.com/") to "fa-brands fa-google-drive",
        Regex("^https://discord\\.(?:com|gg)/") to "fa-brands fa-discord",
    )

    override fun render(node: Link, context: NodeRendererContext, html: HtmlWriter) {
        if (node.url.startsWith("http") && !node.url.startsWith("https:")) {
            logger.warn("Non-HTTPS link found: ${node.url}. Replacing with HTTPS.")
            node.url = node.url.replace("http:", "https:")
        }

        html.attr("href", node.url)
        html.attr("target", "_blank")
        html.withAttr().tag("a") {
            context.renderChildren(node)

            for ((pattern, clazz) in regexToIcon) {
                if (pattern.containsMatchIn(node.url)) {
                    html.tag("sup") {
                        html.attr("class", clazz)
                        html.withAttr().tag("i") {}
                    }

                    break
                }
            }
        }
    }
}
