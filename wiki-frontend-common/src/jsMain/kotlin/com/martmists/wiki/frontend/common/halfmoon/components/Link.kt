package com.martmists.wiki.frontend.halfmoon.components

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLAnchorElement

@Composable
fun HmLink(href: String, underline: Boolean = false, attrs: AttrBuilderContext<HTMLAnchorElement>? = null, content: ComposeBody<HTMLAnchorElement>) {
    A(href = href, attrs = {
        if (underline) {
            classes("hyperlink-underline")
        } else {
            classes("hyperlink")
        }
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmLink(text: String, href: String, underline: Boolean = false, attrs: AttrBuilderContext<HTMLAnchorElement>? = null) {
    HmLink(href = href, underline = underline, attrs = attrs) {
        Text(text)
    }
}
