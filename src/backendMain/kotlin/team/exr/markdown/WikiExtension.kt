package team.exr.markdown

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.util.data.MutableDataHolder

object WikiExtension : HtmlRenderer.HtmlRendererExtension {
    override fun rendererOptions(options: MutableDataHolder) {
        // NA
    }

    override fun extend(htmlRendererBuilder: HtmlRenderer.Builder, rendererType: String) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
            htmlRendererBuilder.attributeProviderFactory(WikiAttributeProvider.Factory())
            htmlRendererBuilder.nodeRendererFactory(WikiNodeRenderer.Factory())
        }
    }
}
