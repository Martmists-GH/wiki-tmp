@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.enum.HmDropdownAlignment
import com.martmists.wiki.frontend.halfmoon.enum.HmDropdownDirection
import com.martmists.wiki.frontend.halfmoon.util.HmDropdownElement
import com.martmists.wiki.frontend.halfmoon.util.HmDropdownMenuElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement

@Composable
fun HmDropdown(attrs: AttrBuilderContext<HmDropdownElement>? = null, content: ComposeBody<HmDropdownElement>) {
    Div(attrs = {
        classes("dropdown")
        attrs?.invoke(this as AttrsScope<HmDropdownElement>)
    }) {
        content(this as ElementScope<HmDropdownElement>)
    }
}

@Composable
fun ElementScope<HmDropdownElement>.HmDropdownButton(dropdownId: String, attrs: AttrBuilderContext<HTMLButtonElement>? = null, content: ComposeBody<HTMLButtonElement>) {
    Button(attrs = {
        classes("btn")
        type(ButtonType.Button)
        attr("data-toggle", "dropdown")
        attr("aria-haspopup", "true")
        attr("aria-expanded", "false")
        id(dropdownId)
        attrs?.invoke(this)
    }, content = content)
}
@Composable
fun ElementScope<HmDropdownElement>.HmDropdownMenu(dropdownId: String, attrs: AttrBuilderContext<HmDropdownMenuElement>? = null, content: ComposeBody<HmDropdownMenuElement>) {
    Div(attrs = {
        classes("dropdown-menu")
        attr("aria-labelledby", dropdownId)
        attrs?.invoke(this as AttrsScope<HmDropdownMenuElement>)
    }) {
        content(this as ElementScope<HmDropdownMenuElement>)
    }
}

@Composable
fun ElementScope<HmDropdownMenuElement>.HmDropdownHeader(attrs: AttrBuilderContext<HTMLHeadingElement>? = null, content: ComposeBody<HTMLHeadingElement>) {
    Div(attrs = {
        classes("dropdown-header")
        attrs?.invoke(this as AttrsScope<HTMLHeadingElement>)
    }) {
        content(this as ElementScope<HTMLHeadingElement>)
    }
}

@Composable
fun ElementScope<HmDropdownMenuElement>.HmDropdownDivider() {
    Div(attrs = {
        classes("dropdown-divider")
    })
}

@Composable
fun ElementScope<HmDropdownMenuElement>.HmDropdownItem(href: String, attrs: AttrBuilderContext<HTMLAnchorElement>? = null, content: ComposeBody<HTMLAnchorElement>) {
    A(href = href, attrs = {
        classes("dropdown-item")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmDropdownMenuElement>.HmDropdownContent(attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("dropdown-content")
        attrs?.invoke(this)
    }, content = content)
}

fun AttrsScope<HmDropdownElement>.toggleOnHover() {
    classes("toggle-on-hover")
}

fun AttrsScope<HmDropdownElement>.direction(type: HmDropdownDirection) {
    when (type) {
        HmDropdownDirection.DOWN -> {}
        HmDropdownDirection.UP -> classes("dropup")
        HmDropdownDirection.LEFT -> classes("dropleft")
        HmDropdownDirection.RIGHT -> classes("dropright")
    }
}

fun AttrsScope<HmDropdownMenuElement>.align(type: HmDropdownAlignment) {
    when (type) {
        HmDropdownAlignment.DOWN, HmDropdownAlignment.LEFT -> {}
        HmDropdownAlignment.UP -> classes("dropdown-menu-up")
        HmDropdownAlignment.CENTER -> classes("dropdown-menu-center")
        HmDropdownAlignment.RIGHT -> classes("dropdown-menu-right")
    }
}
