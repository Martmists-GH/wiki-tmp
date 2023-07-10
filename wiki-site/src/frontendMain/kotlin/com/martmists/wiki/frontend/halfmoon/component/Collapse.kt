@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.util.HmCollapseElement
import com.martmists.wiki.frontend.halfmoon.util.HmCollapseHeaderElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLDetailsElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

@Composable
fun HmCollapse(open: Boolean, attrs: AttrBuilderContext<HmCollapseElement>? = null, content: ComposeBody<HmCollapseElement>) {
    TagElement<HTMLDetailsElement>("details", {
        classes("collapse-panel")
        if (open) {
            attr("open", "true")
        }
        attrs?.invoke(this as AttrsScope<HmCollapseElement>)
    }) {
        content(this as ElementScope<HmCollapseElement>)
    }
}

@Composable
fun ElementScope<HmCollapseElement>.HmCollapseHeader(attrs: AttrBuilderContext<HmCollapseHeaderElement>? = null, content: ComposeBody<HmCollapseHeaderElement>) {
    TagElement<HTMLElement>("summary", {
        classes("collapse-header")
        attrs?.invoke(this as AttrsScope<HmCollapseHeaderElement>)
    }) {
        content(this as ElementScope<HmCollapseHeaderElement>)
    }
}

@Composable
fun ElementScope<HmCollapseElement>.HmCollapseContent(attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("collapse-content")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmCollapseGroup(attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("collapse-group")
        attrs?.invoke(this)
    }, content = content)
}

fun AttrsScope<HmCollapseHeaderElement>.withoutArrow() {
    classes("without-arrow")
}
