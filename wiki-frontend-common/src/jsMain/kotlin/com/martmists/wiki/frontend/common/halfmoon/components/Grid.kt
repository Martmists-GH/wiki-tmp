@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.components

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.enums.HmBreakpoint
import com.martmists.wiki.frontend.halfmoon.utils.HmRowElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.HTMLDivElement

@Composable
fun HmRow(attrs: AttrBuilderContext<HmRowElement>? = null, content: ComposeBody<HmRowElement>) {
    Div(attrs = {
        classes("row")
        attrs?.invoke(this as AttrsScope<HmRowElement>)
    }) {
        content(this as ElementScope<HmRowElement>)
    }
}

@Composable
fun ElementScope<HmRowElement>.HmCol(attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("col")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmRowElement>.HmCol(size: Int, attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    require(size in 1..12)

    Div(attrs = {
        classes("col-$size")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun ElementScope<HmRowElement>.HmCol(vararg sizes: Pair<Int, HmBreakpoint?>, attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    require(sizes.all { it.first in 1..12 })
    require(sizes.distinctBy { it.second }.size == sizes.size)

    Div(attrs = {
        for ((size, brk) in sizes) {
            if (brk == null) {
                classes("col-$size")
            } else {
                classes("col-${brk.name.lowercase()}-$size")
            }
        }
        attrs?.invoke(this)
    }, content = content)
}

fun AttrsScope<HmRowElement>.eqSpacing(breakpoint: HmBreakpoint?) {
    if (breakpoint == null) {
        classes("row-eq-spacing")
    } else {
        classes("row-eq-spacing-${breakpoint.name.lowercase()}")
    }
}
