@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.util.HmPaginationElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLLIElement

@Composable
fun HmPagination(navAttrs: AttrBuilderContext<HTMLElement>? = null, attrs: AttrBuilderContext<HmPaginationElement>? = null, content: ComposeBody<HmPaginationElement>) {
    Nav(attrs = navAttrs) {
        Ul(attrs = {
            classes("pagination")
            attrs?.invoke(this as AttrsScope<HmPaginationElement>)
        }) {
            content(this as ElementScope<HmPaginationElement>)
        }
    }
}

@Composable
fun ElementScope<HmPaginationElement>.HmPaginationItemEllipsis() {
    Li(attrs = {
        classes("page-item", "ellipsis")
    })
}

@Composable
fun ElementScope<HmPaginationElement>.HmPaginationItem(href: String? = "#", active: Boolean = false, disabled: Boolean = false, liAttrs: AttrBuilderContext<HTMLLIElement>? = null, attrs: AttrBuilderContext<HTMLAnchorElement>? = null, content: ComposeBody<HTMLAnchorElement>) {
    Li(attrs = {
        classes("page-item")
        if (active) {
            classes("active")
        }
        if (disabled) {
            classes("disabled")
        }
        liAttrs?.invoke(this)
    }) {
        A(href = href, attrs = {
            classes("page-link")
            attrs?.invoke(this)
        }, content = content)
    }
}

fun AttrsScope<HmPaginationElement>.rounded() {
    classes("pagination-rounded")
}

fun AttrsScope<HmPaginationElement>.large() {
    classes("pagination-lg")
}

fun AttrsScope<HmPaginationElement>.small() {
    classes("pagination-sm")
}
