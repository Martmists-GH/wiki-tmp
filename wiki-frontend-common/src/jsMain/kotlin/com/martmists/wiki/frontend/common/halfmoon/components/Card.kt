@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.components

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.utils.HmCardElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLHeadingElement

@Composable
fun HmCard(attrs: AttrBuilderContext<HmCardElement>? = null, content: ComposeBody<HmCardElement>) {
    Div(attrs = {
        classes("card")
        attrs?.invoke(this as AttrsScope<HmCardElement>)
    }) {
        content(this as ElementScope<HmCardElement>)
    }
}

@Composable
fun ElementScope<HmCardElement>.HmCardTitle(attrs: AttrBuilderContext<HTMLHeadingElement>? = null, content: ComposeBody<HTMLHeadingElement>) {
    H2(attrs = {
        classes("card-title")
        attrs?.invoke(this)
    }, content = content)
}
