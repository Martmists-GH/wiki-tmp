package com.martmists.wiki.frontend.fontawesome

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.I

@Composable
fun FaIcon(icon: String) {
    I(attrs = {
        classes(icon.split(" "))
        attr("aria-hidden", "true")
    })
}
