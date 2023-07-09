@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.components

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.utils.HmModalElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.HTMLSpanElement

@Composable
fun HmModal(modalId: String,
            scrollable: Boolean = false, full: Boolean = false,
            dismissOutside: Boolean = true, dismissEsc: Boolean = true,
            modalAttrs: AttrBuilderContext<HTMLDivElement>? = null, dialogAttrs: AttrBuilderContext<HTMLDivElement>? = null, attrs: AttrBuilderContext<HmModalElement>? = null, content: ComposeBody<HmModalElement>) {
    Div(attrs = {
        classes("modal")
        if (scrollable) {
            classes("ie-scroll-fix")
        }
        if (full) {
            classes("modal-full")
        }
        attr("id", modalId)
        attr("tabindex", "-1")
        attr("role", "dialog")
        if (!dismissOutside) {
            attr("data-overlay-dismissal-disabled", "true")
        }
        if (!dismissEsc) {
            attr("data-esc-dismissal-disabled", "true")
        }
        modalAttrs?.invoke(this)
    }) {
        Div(attrs = {
            classes("modal-dialog")
            attr("role", "document")
            dialogAttrs?.invoke(this)
        }) {
            Div(attrs = {
                classes("modal-content")
                attrs?.invoke(this as AttrsScope<HmModalElement>)
            }) {
                content(this as ElementScope<HmModalElement>)
            }
        }
    }
}

@Composable
fun ElementScope<HmModalElement>.HmModalCloseButton(buttonAttrs: AttrBuilderContext<HTMLAnchorElement>? = null, attrs: AttrBuilderContext<HTMLSpanElement>? = null, content: ComposeBody<HTMLSpanElement> = { Text("&times;") }) {
    A(href = "#", attrs = {
        classes("close")
        attr("role", "button")
        attr("aria-label", "Close")
        buttonAttrs?.invoke(this)
    }) {
        Span(attrs = {
            attr("aria-hidden", "true")
            attrs?.invoke(this)
        }, content = content)
    }
}

@Composable
fun ElementScope<HmModalElement>.HmModalTitle(attrs: AttrBuilderContext<HTMLHeadingElement>? = null, content: ComposeBody<HTMLHeadingElement>) {
    H5(attrs = {
        classes("modal-title")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmModalOpenButton(modalId: String, attrs: AttrBuilderContext<HTMLAnchorElement>? = null, content: ComposeBody<HTMLAnchorElement>) {
    A(href = "#$modalId", attrs = {
        classes("btn")
        attr("data-toggle", "modal")
        attr("role", "button")
        attrs?.invoke(this)
    }, content = content)
}

fun AttrsScope<HmModalElement>.media() {
    classes("modal-content-media")
}
