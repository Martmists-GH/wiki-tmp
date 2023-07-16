@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.enum.HmAlertFillMode
import com.martmists.wiki.frontend.halfmoon.enum.HmAlertType
import com.martmists.wiki.frontend.halfmoon.util.HmAlertElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.HTMLSpanElement

@Composable
fun HmAlert(type: HmAlertType? = null, attrs: AttrBuilderContext<HmAlertElement>? = null, content: ComposeBody<HmAlertElement>) {
    Div(attrs = {
        classes("alert")
        if (type != null) {
            classes("alert-${type.name.lowercase()}")
        }
        attr("role", "alert")
        attrs?.invoke(this as AttrsScope<HmAlertElement>)
    }) {
        content(this as ElementScope<HmAlertElement>)
    }
}

@Composable
fun ElementScope<HmAlertElement>.HmAlertHeading(attrs: AttrBuilderContext<HTMLHeadingElement>? = null, content: ComposeBody<HTMLHeadingElement>) {
    H4(attrs = {
        classes("alert-title")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmAlertElement>.HmAlertLink(href: String, attrs: AttrBuilderContext<HTMLAnchorElement>? = null, content: ComposeBody<HTMLAnchorElement>) {
    A(href = href, attrs = {
        classes("alert-link")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmAlertElement>.HmAlertDismissButton(buttonAttrs: AttrBuilderContext<HTMLButtonElement>? = null, attrs: AttrBuilderContext<HTMLSpanElement>? = null, content: ComposeBody<HTMLSpanElement> = { Text("&times;") }) {
    Button(attrs = {
        classes("close")
        type(ButtonType.Button)
        attr("data-dismiss", "alert")
        attr("aria-label", "Close")
        buttonAttrs?.invoke(this)
    }) {
        Span(attrs = {
            attr("aria-hidden", "true")
            attrs?.invoke(this)
        }, content = content)
    }
}

fun AttrsScope<HmAlertElement>.filled(mode: HmAlertFillMode) {
    when (mode) {
        HmAlertFillMode.LIGHT_ONLY -> classes("filled-lm")
        HmAlertFillMode.DARK_ONLY -> classes("filled-dm")
        HmAlertFillMode.ALL -> classes("filled")
    }
}
