package com.martmists.wiki.frontend.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.ElementScope

typealias ComposeBody<T> = @Composable ElementScope<T>.() -> Unit
