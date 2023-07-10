@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.enum.HmBadgeType
import com.martmists.wiki.frontend.halfmoon.util.HmBadgeElement
import com.martmists.wiki.frontend.halfmoon.util.HmLinkBadgeElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLSpanElement

@Composable
fun HmBadge(type: HmBadgeType? = null, attrs: AttrBuilderContext<HmBadgeElement>? = null, content: ComposeBody<HmBadgeElement>) {
    Span(attrs = {
        classes("badge")
        if (type != null) {
            classes("badge-${type.name.lowercase()}")
        }
        attrs?.invoke(this as AttrsScope<HmBadgeElement>)
    }) {
        content(this as ElementScope<HmBadgeElement>)
    }
}

@Composable
fun HmBadge(text: String, type: HmBadgeType? = null, attrs: AttrBuilderContext<HmBadgeElement>? = null) {
    HmBadge(type = type, attrs = attrs) {
        Text(text)
    }
}

@Composable
fun HmLinkBadge(type: HmBadgeType? = null, attrs: AttrBuilderContext<HmLinkBadgeElement>? = null, content: ComposeBody<HmLinkBadgeElement>) {
    A(attrs = {
        classes("badge")
        if (type != null) {
            classes("badge-${type.name.lowercase()}")
        }
        attrs?.invoke(this as AttrsScope<HmLinkBadgeElement>)
    }) {
        content(this as ElementScope<HmLinkBadgeElement>)
    }
}

@Composable
fun HmLinkBadge(text: String, type: HmBadgeType? = null, attrs: AttrBuilderContext<HmLinkBadgeElement>? = null) {
    HmLinkBadge(type = type, attrs = attrs) {
        Text(text)
    }
}

@Composable
fun HmBadgeGroup(attrs: AttrBuilderContext<HTMLSpanElement>? = null, content: ComposeBody<HTMLSpanElement>) {
    Span(attrs = {
        classes("badge-group")
        attr("role", "group")
        attrs?.invoke(this)
    }, content = content)
}

fun <T> AttrsScope<T>.pill() where T : HmBadgeElement, T : ElementScope<T> {
    classes("badge-pill")
}
