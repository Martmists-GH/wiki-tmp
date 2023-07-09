@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.components

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.utils.HmNavbarElement
import com.martmists.wiki.frontend.halfmoon.utils.HmNavbarNavigationElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.*

@Composable
fun HmNavbar(attrs: AttrBuilderContext<HmNavbarElement>? = null, content: ComposeBody<HmNavbarElement>) {
    Nav(attrs = {
        classes("navbar")
        attrs?.invoke(this as AttrsScope<HmNavbarElement>)
    }) {
        content(this as ElementScope<HmNavbarElement>)
    }
}

@Composable
fun ElementScope<HmNavbarElement>.HmNavbarContent(attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("navbar-content")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmNavbarElement>.HmNavbarBrand(href: String = "#", attrs: AttrBuilderContext<HTMLAnchorElement>? = null, content: ComposeBody<HTMLAnchorElement>) {
    A(href = href, attrs = {
        classes("navbar-brand")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmNavbarElement>.HmNavbarBrand(name: String, imgSrc: String, imgAlt: String = "", href: String = "#", attrs: AttrBuilderContext<HTMLAnchorElement>? = null, imgAttrs: AttrBuilderContext<HTMLImageElement>) {
    HmNavbarBrand(href = href, attrs = attrs) {
        Img(src = imgSrc, alt = imgAlt, attrs = imgAttrs)
        Text(name)
    }
}

@Composable
fun ElementScope<HmNavbarElement>.HmNavbarText(attrs: AttrBuilderContext<HTMLSpanElement>? = null, content: ComposeBody<HTMLSpanElement>) {
    Span(attrs = {
        classes("navbar-text")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmNavbarElement>.HmNavbarNavigation(attrs: AttrBuilderContext<HmNavbarNavigationElement>? = null, content: ComposeBody<HmNavbarNavigationElement>) {
    Ul(attrs = {
        classes("navbar-nav")
        attrs?.invoke(this as AttrsScope<HmNavbarNavigationElement>)
    }) {
        content(this as ElementScope<HmNavbarNavigationElement>)
    }
}

@Composable
fun ElementScope<HmNavbarNavigationElement>.HmNavbarNavigationItem(active: Boolean = false, attrs: AttrBuilderContext<HTMLLIElement>? = null, content: ComposeBody<HTMLLIElement>) {
    Li(attrs = {
        classes("nav-item")
        if (active) {
            classes("active")
        }
        attrs?.invoke(this)
    }, content = content)
}


@Composable
fun ElementScope<HmNavbarNavigationElement>.HmNavbarNavigationItem(text: String, href: String, active: Boolean = false, attrs: AttrBuilderContext<HTMLLIElement>? = null, anchorAttrs: AttrBuilderContext<HTMLAnchorElement>? = null) {
    HmNavbarNavigationItem(active = active, attrs = attrs) {
        A(href, attrs = {
            classes("nav-link")
            anchorAttrs?.invoke(this)
        }) {
            Text(text)
        }
    }
}
