@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.components

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.enums.HmTableBorderType
import com.martmists.wiki.frontend.halfmoon.utils.HmTableElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Table


@Composable
fun HmTable(attrs: AttrBuilderContext<HmTableElement>? = null, content: ComposeBody<HmTableElement>) {
    Table(attrs = {
        classes("table")
        attrs?.invoke(this as AttrsScope<HmTableElement>)
    }) {
        content(this as ElementScope<HmTableElement>)
    }
}

@Composable
fun HmResponsiveTable(attrs: AttrBuilderContext<HmTableElement>? = null, content: ComposeBody<HmTableElement>) {
    Div(attrs = {
        classes("table-responsive")
    }) {
        HmTable(attrs = attrs, content = content)
    }
}

fun AttrsScope<HmTableElement>.noOuterPadding() {
    classes("table-no-outer-padding")
}

fun AttrsScope<HmTableElement>.striped() {
    classes("table-striped")
}

fun AttrsScope<HmTableElement>.hover() {
    classes("table-hover")
}

fun AttrsScope<HmTableElement>.bordered(type: HmTableBorderType) {
    when (type) {
        HmTableBorderType.BORDERED -> classes("table-bordered")
        HmTableBorderType.BORDERED_OUTER -> classes("table-outer-bordered")
        HmTableBorderType.BORDERED_INNER -> classes("table-inner-bordered")
    }
}
