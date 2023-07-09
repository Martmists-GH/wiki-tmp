package com.martmists.wiki.frontend.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.HTMLDivElement

typealias ComposeBody<T> = @Composable ElementScope<T>.() -> Unit
