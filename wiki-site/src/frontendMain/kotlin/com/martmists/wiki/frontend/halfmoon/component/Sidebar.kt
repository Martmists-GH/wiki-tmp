@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.fontawesome.FaIcon
import com.martmists.wiki.frontend.halfmoon.util.HmSidebarElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.HTMLImageElement

@Composable
fun HmSidebar(attrs: AttrBuilderContext<HTMLDivElement>? = null, menuAttrs: AttrBuilderContext<HmSidebarElement>? = null, content: ComposeBody<HmSidebarElement>) {
    Div(attrs = {
        classes("sidebar")
        attrs?.invoke(this)
    }) {
        Div(attrs = {
            classes("sidebar-menu")
            menuAttrs?.invoke(this as AttrsScope<HmSidebarElement>)
        }) {
            content(this as ElementScope<HmSidebarElement>)
        }
    }
}

@Composable
fun ElementScope<HmSidebarElement>.HmSidebarBrand(href: String = "#", attrs: AttrBuilderContext<HTMLAnchorElement>? = null, content: ComposeBody<HTMLAnchorElement>) {
    A(href = href, attrs = {
        classes("sidebar-brand")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmSidebarElement>.HmSidebarBrand(name: String, imgSrc: String, imgAlt: String = "", href: String = "#", attrs: AttrBuilderContext<HTMLAnchorElement>? = null, imgAttrs: AttrBuilderContext<HTMLImageElement>) {
    HmSidebarBrand(href = href, attrs = attrs) {
        Img(src = imgSrc, alt = imgAlt, attrs = imgAttrs)
        Text(name)
    }
}

@Composable
fun ElementScope<HmSidebarElement>.HmSidebarTitle(attrs: AttrBuilderContext<HTMLHeadingElement>? = null, content: ComposeBody<HTMLHeadingElement>) {
    H5(attrs = {
        classes("sidebar-title")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmSidebarElement>.HmSidebarDivider() {
    Div(attrs = {
        classes("sidebar-divider")
    })
}

@Composable
fun ElementScope<HmSidebarElement>.HmSidebarLink(href: String, active: Boolean = false, attrs: AttrBuilderContext<HTMLAnchorElement>? = null, content: ComposeBody<HTMLAnchorElement>) {
    A(href = href, attrs = {
        classes("sidebar-link")
        if (active) {
            classes("active")
        }
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmSidebarElement>.HmSidebarLink(name: String, href: String, icon: String? = null, active: Boolean = false, attrs: AttrBuilderContext<HTMLAnchorElement>? = null) {
    HmSidebarLink(href = href, active = active, attrs = {
        if (icon != null) {
            classes("sidebar-link-with-icon")
        }
        attrs?.invoke(this)
    }) {
        if (icon != null) {
            Span(attrs = {
                classes("sidebar-icon")
            }) {
                FaIcon(icon)
            }
        }
        Text(name)
    }
}

@Composable
fun ElementScope<HmSidebarElement>.HmSidebarContent(attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("sidebar-content")
        attrs?.invoke(this)
    }, content = content)
}
