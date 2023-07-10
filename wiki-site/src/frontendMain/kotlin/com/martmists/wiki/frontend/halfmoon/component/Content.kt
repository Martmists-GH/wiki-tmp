@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.util.HmContentElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLHeadingElement

@Composable
fun HmContent(attrs: AttrBuilderContext<HmContentElement>? = null, content: ComposeBody<HmContentElement>) {
    Div(attrs = {
        classes("content")
        attrs?.invoke(this as AttrsScope<HmContentElement>)
    }) {
        content(this as ElementScope<HmContentElement>)
    }
}

@Composable
fun ElementScope<HmContentElement>.HmContentTitle(attrs: AttrBuilderContext<HTMLHeadingElement>? = null, content: ComposeBody<HTMLHeadingElement>) {
    H2(attrs = {
        classes("content-title")
        attrs?.invoke(this)
    }, content = content)
}
