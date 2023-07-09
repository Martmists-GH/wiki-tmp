package com.martmists.wiki.frontend.halfmoon.components

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.enums.HmContainerSize
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

@Composable
fun HmContainer(size: HmContainerSize? = null, attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        if (size != null) {
            classes("container-${size.name.lowercase()}")
        } else {
            classes("container")
        }
        attrs?.invoke(this)
    }, content = content)
}
