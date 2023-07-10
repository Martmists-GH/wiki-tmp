@file:Suppress("UNCHECKED_CAST")

package com.martmists.wiki.frontend.halfmoon.component

import androidx.compose.runtime.Composable
import com.martmists.wiki.frontend.halfmoon.util.HmAButtonElement
import com.martmists.wiki.frontend.halfmoon.util.HmButton
import com.martmists.wiki.frontend.halfmoon.util.HmButtonElement
import com.martmists.wiki.frontend.util.ComposeBody
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.builders.InputAttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

@Composable
fun HmButton(attrs: AttrBuilderContext<HmButtonElement>? = null, content: ComposeBody<HmButtonElement>) {
    Button(attrs = {
        classes("btn")
        attrs?.invoke(this as AttrsScope<HmButtonElement>)
    }) {
        content(this as ElementScope<HmButtonElement>)
    }
}

@Composable
fun HmButtonPrimary(attrs: AttrBuilderContext<HmButtonElement>? = null, content: ComposeBody<HmButtonElement>) {
    HmButton(attrs = {
        classes("btn-primary")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmButtonSecondary(attrs: AttrBuilderContext<HmButtonElement>? = null, content: ComposeBody<HmButtonElement>) {
    HmButton(attrs = {
        classes("btn-secondary")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmButtonSuccess(attrs: AttrBuilderContext<HmButtonElement>? = null, content: ComposeBody<HmButtonElement>) {
    HmButton(attrs = {
        classes("btn-success")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmButtonDanger(attrs: AttrBuilderContext<HmButtonElement>? = null, content: ComposeBody<HmButtonElement>) {
    HmButton(attrs = {
        classes("btn-danger")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmButtonLink(attrs: AttrBuilderContext<HmButtonElement>? = null, content: ComposeBody<HmButtonElement>) {
    HmButton(attrs = {
        classes("btn-link")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmInputButton(type: InputType<Unit>, attrs: (InputAttrsScope<Unit>.() -> Unit)? = null) {
    require(type in listOf(InputType.Button, InputType.Submit))

    Input(type, attrs = {
        classes("btn")
        attrs?.invoke(this)
    })
}

@Composable
fun HmInputButtonPrimary(type: InputType<Unit>, attrs: (InputAttrsScope<Unit>.() -> Unit)? = null) {
    HmInputButton(type, attrs = {
        classes("btn-primary")
        attrs?.invoke(this)
    })
}

@Composable
fun HmInputButtonSecondary(type: InputType<Unit>, attrs: (InputAttrsScope<Unit>.() -> Unit)? = null) {
    HmInputButton(type, attrs = {
        classes("btn-secondary")
        attrs?.invoke(this)
    })
}

@Composable
fun HmInputButtonSuccess(type: InputType<Unit>, attrs: (InputAttrsScope<Unit>.() -> Unit)? = null) {
    HmInputButton(type, attrs = {
        classes("btn-success")
        attrs?.invoke(this)
    })
}

@Composable
fun HmInputButtonDanger(type: InputType<Unit>, attrs: (InputAttrsScope<Unit>.() -> Unit)? = null) {
    HmInputButton(type, attrs = {
        classes("btn-danger")
        attrs?.invoke(this)
    })
}

@Composable
fun HmInputButtonLink(type: InputType<Unit>, attrs: (InputAttrsScope<Unit>.() -> Unit)? = null) {
    HmInputButton(type, attrs = {
        classes("btn-link")
        attrs?.invoke(this)
    })
}

@Composable
fun HmAButton(href: String, attrs: AttrBuilderContext<HmAButtonElement>? = null, content: ComposeBody<HmAButtonElement>) {
    A(href = href, attrs = {
        classes("btn")
        attr("role", "button")
        attrs?.invoke(this as AttrsScope<HmAButtonElement>)
    }) {
        content(this as ElementScope<HmAButtonElement>)
    }
}

@Composable
fun HmAButtonPrimary(href: String, attrs: AttrBuilderContext<HmAButtonElement>? = null, content: ComposeBody<HmAButtonElement>) {
    HmAButton(href = href, attrs = {
        classes("btn-primary")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmAButtonSecondary(href: String, attrs: AttrBuilderContext<HmAButtonElement>? = null, content: ComposeBody<HmAButtonElement>) {
    HmAButton(href = href, attrs = {
        classes("btn-secondary")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmAButtonSuccess(href: String, attrs: AttrBuilderContext<HmAButtonElement>? = null, content: ComposeBody<HmAButtonElement>) {
    HmAButton(href = href, attrs = {
        classes("btn-success")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmAButtonDanger(href: String, attrs: AttrBuilderContext<HmAButtonElement>? = null, content: ComposeBody<HmAButtonElement>) {
    HmAButton(href = href, attrs = {
        classes("btn-danger")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmAButtonLink(href: String, attrs: AttrBuilderContext<HmAButtonElement>? = null, content: ComposeBody<HmAButtonElement>) {
    HmAButton(href = href, attrs = {
        classes("btn-link")
        attrs?.invoke(this)
    }, content = content)
}

@Composable
fun HmButtonGroup(attrs: AttrBuilderContext<HTMLDivElement>? = null, content: ComposeBody<HTMLDivElement>) {
    Div(attrs = {
        classes("btn-group")
        attr("role", "group")
        attrs?.invoke(this)
    }) {
        content(this)
    }
}

// TODO: HmButtonGroup with Dropdown

fun <T> AttrsScope<T>.action() where T : HmButton, T : Element {
    classes("btn-action")
}

fun <T> AttrsScope<T>.small() where T : HmButton, T : Element {
    classes("btn-sm")
}

fun <T> AttrsScope<T>.large() where T : HmButton, T : Element {
    classes("btn-lg")
}

fun <T> AttrsScope<T>.block() where T : HmButton, T : Element {
    classes("btn-block")
}

fun <T> AttrsScope<T>.disabled() where T : HmButton, T : Element {
    classes("disabled")
    attr("disabled", "disabled")
}

fun <T> AttrsScope<T>.active() where T : HmButton, T : Element {
    classes("active")
}

fun <T> AttrsScope<T>.altDark() where T : HmButton, T : Element {
    classes("alt-dm")
}

fun <T> AttrsScope<T>.square() where T : HmButton, T : Element {
    classes("btn-square")
}

fun <T> AttrsScope<T>.rounded() where T : HmButton, T : Element {
    classes("btn-rounded")
}
