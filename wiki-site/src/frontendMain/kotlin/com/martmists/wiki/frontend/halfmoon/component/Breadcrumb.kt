@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.util.HmBreadcrumbElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

@Composable
fun HmBreadcrumb(containerAttrs: AttrBuilderContext<HTMLElement>, attrs: AttrBuilderContext<HmBreadcrumbElement>? = null, content: ComposeBody<HmBreadcrumbElement>) {
    Nav(attrs = containerAttrs) {
        Ul(attrs = {
            classes("breadcrumb")
            attrs?.invoke(this as AttrsScope<HmBreadcrumbElement>)
        }) {
            content(this as ElementScope<HmBreadcrumbElement>)
        }
    }
}


@Composable
fun ElementScope<HmBreadcrumbElement>.HmBreadcrumbItem(name: String, href: String, active: Boolean = false) {
    Li(attrs = {
        classes("breadcrumb-item")
        if (active) {
            classes("active")
            attr("aria-current", "page")
        }
    }) {
        A(href = href) {
            Text(name)
        }
    }
}
