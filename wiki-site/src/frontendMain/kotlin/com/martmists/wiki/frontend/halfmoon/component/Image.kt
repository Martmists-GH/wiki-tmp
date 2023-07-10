@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.util.HmImageElement
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Img

@Composable
fun HmImage(src: String, alt: String = "", attrs: AttrBuilderContext<HmImageElement>? = null) {
    Img(src = src, alt = alt, attrs = {
        classes("img-fluid")
        attrs?.invoke(this as AttrsScope<HmImageElement>)
    })
}

fun AttrsScope<HmImageElement>.rounded() {
    classes("rounded")
}

fun AttrsScope<HmImageElement>.roundedCircle() {
    classes("rounded-circle")
}

fun AttrsScope<HmImageElement>.roundedTop() {
    classes("rounded-top")
}

fun AttrsScope<HmImageElement>.roundedRight() {
    classes("rounded-right")
}

fun AttrsScope<HmImageElement>.roundedBottom() {
    classes("rounded-bottom")
}

fun AttrsScope<HmImageElement>.roundedLeft() {
    classes("rounded-left")
}
