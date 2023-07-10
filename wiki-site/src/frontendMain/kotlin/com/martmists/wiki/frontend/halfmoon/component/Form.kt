@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.enum.HmBreakpoint
import com.martmists.wiki.frontend.halfmoon.util.HmFormElement
import org.w3c.dom.HTMLDivElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope

@Composable
fun HmForm(inline: Boolean = false, attrs: AttrBuilderContext<HmFormElement>? = null, content: ComposeBody<HmFormElement>) {
    Div(attrs = {
        if (inline) {
            classes("form-inline")
        }
        attrs?.invoke(this as AttrsScope<HmFormElement>)
    }) {
        content(this as ElementScope<HmFormElement>)
    }
}

@Composable
fun ElementScope<HmFormElement>.HmFormGroup(invalid: Boolean = false, attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("form-group")
        if (invalid) {
            classes("is-invalid")
        }
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmFormElement>.HmFormRow(size: HmBreakpoint? = null, attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("form-row")
        if (size == null) {
            classes("row-eq-spacing")
        } else {
            classes("row-eq-spacing-${size.name.lowercase()}")
        }
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmFormElement>.HmFormText(attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("form-text")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmFormElement>.HmFormInvalidFeedback(attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("invalid-feedback")
        attrs?.invoke(this)
    }, content = content)
}

// TODO: Form components but fuck that
