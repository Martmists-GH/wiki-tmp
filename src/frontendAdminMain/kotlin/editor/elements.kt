@file:Suppress("UNCHECKED_CAST")

package editor

import ext.*
import kotlinx.browser.document
import httpClient
import hljs
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import js.core.jso
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.*
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import kotlin.js.json

fun `editor$elements`() {
    val wikiSelect = document.querySelector<HTMLSelectElement>("#wjs-wiki-id") {
        onchange = {
            val (wikiId, groupId, public) = value.split(":")

            document.dispatchEvent(CustomEvent("wiki:wiki_id_change", jso {
                detail = Triple(wikiId, groupId, public)
            }))
        }
    }

    val groupSelect = document.querySelector<HTMLSelectElement>("#wjs-group-id") {
        onchange = {
            document.dispatchEvent(CustomEvent("wiki:group_id_change", jso {
                detail = value
            }))
        }

        document.addEventListener<CustomEvent>("wiki:wiki_id_change") { event ->
            val (_, groupId, _) = event.detail as Triple<String, String, String>

            disabled = false

            updateValue(groupId)
        }
    }

    document.querySelector<HTMLInputElement>("#wjs-group-name") {
        document.addEventListener<CustomEvent>("wiki:group_id_change") { event ->
            val groupId = event.detail as String
            disabled = false

            updateValue(groupSelect!!.options.asList().filterIsInstance<HTMLOptionElement>().first { it.value == groupId }.text)
        }
    }

    document.querySelector<HTMLInputElement>("#wjs-wiki-title") {
        document.addEventListener<CustomEvent>("wiki:wiki_id_change") { event ->
            val (wikiId, _, _) = event.detail as Triple<String, String, String>

            disabled = false
            if (wikiId != "-2") {
                updateValue(wikiSelect!!.options.asList().filterIsInstance<HTMLOptionElement>().first { it.value.split(":").first() == wikiId }.text)
            }
        }
    }

    document.querySelector<HTMLInputElement>("#wjs-wiki-public") {
        document.addEventListener<CustomEvent>("wiki:wiki_id_change") { event ->
            val (_, _, public) = event.detail as Triple<String, String, String>

            disabled = false
            checked = public == "true"
        }
    }

    document.querySelector<HTMLInputElement>("#wjs-wiki-save") {
        document.addEventListener<CustomEvent>("wiki:wiki_id_change") {
            disabled = false
        }
    }

    document.querySelector<HTMLInputElement>("#wjs-wiki-delete") {
        addEventListener<MouseEvent>("click") {
            document.querySelector<HTMLInputElement>("#wjs-wiki-delete-check")?.checked = true
        }

        document.addEventListener<CustomEvent>("wiki:wiki_id_change") { event ->
            val (wikiId, _, _) = event.detail as Triple<String, String, String>

            if (wikiId == "-2") {
                disabled = true
                classList.add("d-none")
            } else {
                disabled = false
                classList.remove("d-none")
            }
        }
    }

    document.querySelector<HTMLButtonElement>("#wjs-wiki-preview") {
        var id = ""

        document.addEventListener<CustomEvent>("wiki:wiki_id_change") {
            val (wikiId, _, _) = it.detail as Triple<String, String, String>

            disabled = wikiId == "-2"
            id = wikiId
        }

        onclick = {
            window.open("/admin/wiki/$id/markdown", "_blank")
        }
    }

    document.querySelector<HTMLTextAreaElement>("#wjs-wiki-content") {
        val code = document.querySelector("#wjs-wiki-code")!!

        val trigger = inactiveTrigger(500) {
            GlobalScope.launch {
                hljs.highlightElement(code)

                code.querySelectorAll<HTMLSpanElement>(".hljs-code") {
                    if (innerText.startsWith("```")) {
                        val lang = innerText.substringAfter("```").substringBefore("\n")
                        GlobalScope.launch {
                            innerHTML = "```$lang\n${hljs.highlight(innerText.substringAfter("```$lang\n").substringBeforeLast("\n```"), json("language" to lang)).value}\n```"
                        }
                    }
                }
            }
        }

        oninput = {
            code.innerHTML = value.escapeHTML()
            trigger()
        }
        onchange = {
            code.innerHTML = value.escapeHTML()
            trigger()
        }
        onscroll = {
            code.scrollTop = this.scrollTop
            code.scrollLeft = this.scrollLeft
            Unit
        }

        addEventListener<KeyboardEvent>("keydown") {
            if (it.key == "Tab") {
                it.preventDefault()
                val start = selectionStart!!
                val end = selectionEnd!!
                val before = value.substring(0, start)
                val lines = before.split("\n").toMutableList()
                val lastLine = lines.last()

                if (it.shiftKey) {
                    // dedent
                    if (lastLine.startsWith("    ")) {
                        lines[lines.lastIndex] = lastLine.substring(4)
                        updateValue(lines.joinToString("\n") + value.substring(start))
                        selectionStart = start - 4
                        selectionEnd = end - 4
                    }
                } else {
                    // indent
                    lines[lines.lastIndex] = "    $lastLine"
                    updateValue(lines.joinToString("\n") + value.substring(start))
                    selectionStart = start + 4
                    selectionEnd = end + 4
                }
            }
        }

        document.addEventListener<CustomEvent>("wiki:wiki_id_change") {
            val (wikiId, _, _) = it.detail as Triple<String, String, String>

            disabled = false

            if (wikiId != "-2") {
                GlobalScope.launch {
                    val res = httpClient.get("/admin/wiki/$id/markdown")
                    this@querySelector.updateValue(res.bodyAsText())
                }
            }
        }
    }

    // TODO: Images
}
