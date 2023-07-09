@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.components

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.enums.HmProgressbarType
import com.martmists.wiki.frontend.halfmoon.utils.HmProgressBarElement
import com.martmists.wiki.frontend.halfmoon.utils.HmProgressElement
import com.martmists.wiki.frontend.halfmoon.utils.HmProgressGroupElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Span
import org.w3c.dom.HTMLSpanElement

@Composable
fun HmProgress(attrs: AttrBuilderContext<HmProgressElement>? = null, content: ComposeBody<HmProgressElement>) {
    Div(attrs = {
        classes("progress")
        attrs?.invoke(this as AttrsScope<HmProgressElement>)
    }) {
        content(this as ElementScope<HmProgressElement>)
    }
}

@Composable
fun ElementScope<HmProgressElement>.HmProgressBar(value: Int, min: Int = 0, max: Int = 100, type: HmProgressbarType? = null, attrs: AttrBuilderContext<HmProgressBarElement>? = null, content: ComposeBody<HmProgressBarElement>) {
    Div(attrs = {
        classes("progress-bar")
        attr("role", "progressbar")
        attr("aria-valuenow", value.toString())
        attr("aria-valuemin", min.toString())
        attr("aria-valuemax", max.toString())
        style {
            width(value.percent)
        }
        if (type != null) {
            classes("bg-${type.name.lowercase()}")
        }
        attrs?.invoke(this as AttrsScope<HmProgressBarElement>)
    }) {
        content(this as ElementScope<HmProgressBarElement>)
    }
}

@Composable
fun HmProgressGroup(attrs: AttrBuilderContext<HmProgressGroupElement>? = null, content: ComposeBody<HmProgressGroupElement>) {
    Div(attrs = {
        classes("progress-group")
        attrs?.invoke(this as AttrsScope<HmProgressGroupElement>)
    }) {
        content(this as ElementScope<HmProgressGroupElement>)
    }
}

@Composable
fun ElementScope<HmProgressGroupElement>.HmProgressGroupLabel(attrs: AttrBuilderContext<HTMLSpanElement>? = null, content: ComposeBody<HTMLSpanElement>) {
    Span(attrs = {
        classes("progress-group-label")
        attrs?.invoke(this)
    }, content = content)
}

fun AttrsScope<HmProgressBarElement>.animated(dark: Boolean = false) {
    classes("progress-bar-animated")
    if (dark) {
        classes("highlight-dark")
    }
}
