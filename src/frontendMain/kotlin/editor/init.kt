package editor

import ext.addEventListener
import ext.inactiveTrigger
import ext.querySelector
import ext.querySelectorAll
import hljs
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.InputEvent
import team.exr.markdown.MarkdownTransformer

fun `editor$transformer`() {
    document.querySelector<HTMLDivElement>(".md-editor")?.let { editor ->
        val input = editor.querySelector<HTMLTextAreaElement>(".md-editor-input")!!
        val code = editor.querySelector<HTMLElement>(".md-editor-code")!!
        val rendered = editor.querySelector<HTMLElement>(".md-editor-out")!!

        input.addEventListener("input", inactiveTrigger(100) {
            val text = input.value
            val html = MarkdownTransformer.handle(text)
            rendered.innerHTML = html
            rendered.querySelectorAll<HTMLElement>("code[class*='language-'").forEach {
                hljs.highlightElement(it)
                Unit
            }
        })

        input.addEventListener<InputEvent>("input") {
            var text = input.value
            if (text.isNotEmpty() && text.last() == '\n') {
                text += " "
            }
            code.innerHTML = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") //.replace("\n", "<br>").replace(" ", "&nbsp;")
            hljs.highlightElement(code)
            Unit
        }

        input.addEventListener("scroll", {
            code.scrollTop = input.scrollTop
            code.scrollLeft = input.scrollLeft
        })
    }
}
