package com.martmists.wiki.frontend.halfmoon.components

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLPreElement

@Composable
fun HmCode(attrs: AttrBuilderContext<HTMLElement>? = null, content: ComposeBody<HTMLElement>) {
    Code(attrs = {
        classes("code")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmKeyboardInput(vararg buttons: String) {
    for ((i, button) in buttons.withIndex()) {
        TagElement<HTMLElement>("kbd", null) {
            Text(button)
        }
        if (i != buttons.lastIndex) {
            Text(" + ")
        }
    }
}
