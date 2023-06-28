@file:Suppress("UNCHECKED_CAST")

package editor

import ext.*
import kotlinx.browser.document
import httpClient
import hljs
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import js.core.jso
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.InputEvent
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.url.URL
import org.w3c.xhr.FormData
import team.exr.ext.toUrlString
import team.exr.payloads.EditorGroupMetadataPayload
import team.exr.payloads.EditorMetadataPayload
import kotlin.js.json

private fun HTMLElement.addInputListener(block: (Event) -> Unit) {
    addEventListener<Event>("change") {
        block(it)
    }
    addEventListener<InputEvent>("input") {
        block(it)
    }
}

fun `editor$elements`() {
    val rootIdSelect = document.querySelector<HTMLSelectElement>("#wjs-editor-id-select")

    if (rootIdSelect != null) {
        // Editor exists
        val groupIdSelect = document.querySelector<HTMLSelectElement>("#wjs-editor-group-id")!!
        val groupNameInput = document.querySelector<HTMLInputElement>("#wjs-editor-group-name")!!
        val groupPriorityInput = document.querySelector<HTMLInputElement>("#wjs-editor-group-priority")!!
        val groupSaveButton = document.querySelector<HTMLInputElement>("#wjs-editor-group-btn-save")!!
        val groupDeleteButton = document.querySelector<HTMLInputElement>("#wjs-editor-group-btn-delete")!!
        val pageTitleInput = document.querySelector<HTMLInputElement>("#wjs-editor-page-title")!!
        val pageDescriptionInput = document.querySelector<HTMLInputElement>("#wjs-editor-page-desc")!!
        val pagePriorityInput = document.querySelector<HTMLInputElement>("#wjs-editor-page-priority")!!
        val pagePublicInput = document.querySelector<HTMLInputElement>("#wjs-editor-page-public")!!
        val pageIdInputH = document.querySelector<HTMLInputElement>("#wjs-editor-page-id")!!
        val pageGroupInputH = document.querySelector<HTMLInputElement>("#wjs-editor-page-group")!!
        val pageContentInputH = document.querySelector<HTMLTextAreaElement>("#wjs-editor-page-content")!!
        val pageSaveButton = document.querySelector<HTMLInputElement>("#wjs-editor-page-btn-save")!!
        val pageDeleteButton = document.querySelector<HTMLInputElement>("#wjs-editor-page-btn-delete")!!
        val pagePreviewButton = document.querySelector<HTMLButtonElement>("#wjs-editor-page-btn-preview")!!
        val imageIdSelect = document.querySelector<HTMLSelectElement>("#wjs-editor-image-id")!!
        val imagePreview = document.querySelector<HTMLImageElement>("#wjs-editor-image-preview")!!
        val imageFileInput = document.querySelector<HTMLInputElement>("#wjs-editor-image-file")!!
        val imageNameInput = document.querySelector<HTMLInputElement>("#wjs-editor-image-name")!!
        val imagePathPreview = document.querySelector<HTMLInputElement>("#wjs-editor-image-path")!!
        val imageSaveButton = document.querySelector<HTMLInputElement>("#wjs-editor-image-btn-save")!!
        val imageDeleteButton = document.querySelector<HTMLInputElement>("#wjs-editor-image-btn-delete")!!
        val mdInput = document.querySelector<HTMLTextAreaElement>("#wjs-editor-md-input")!!
        val mdCode = document.querySelector<HTMLElement>("#wjs-editor-md-code")!!

        rootIdSelect.addEventListener<Event>("change") {
            val (wikiId, groupId) = rootIdSelect.value.split(":").map(String::toInt)

            groupIdSelect.disabled = false
            groupIdSelect.updateValue(groupId.toString())

            pageTitleInput.disabled = false
            pageTitleInput.updateValue(rootIdSelect.selectedOptions.item(0)!!.textContent ?: "")
            pageDescriptionInput.disabled = false
            pagePriorityInput.disabled = false
            pagePublicInput.disabled = false
            pageSaveButton.disabled = false

            mdInput.disabled = false

            pageIdInputH.updateValue(wikiId.toString())

            if (wikiId >= 0) {
                pageDeleteButton.disabled = false
                pageDeleteButton.classList.remove("d-none")
                pagePreviewButton.disabled = false

                GlobalScope.launch {
                    val data = httpClient.get("/admin/wiki/$wikiId/metadata").body<EditorMetadataPayload>()
                    pageDescriptionInput.updateValue(data.description)
                    pagePriorityInput.updateValue(data.priority.toString())
                    pagePublicInput.checked = data.published
                }

                GlobalScope.launch {
                    val content = httpClient.get("/admin/wiki/$wikiId/markdown").bodyAsText()
                    mdInput.updateValue(content)
                }
            } else {
                pageDeleteButton.disabled = true
                if (!pageDeleteButton.classList.contains("d-none")) {
                    pageDeleteButton.classList.add("d-none")
                }
                pagePreviewButton.disabled = true

                pageDescriptionInput.updateValue("")
                pagePriorityInput.updateValue("0")
                pagePublicInput.checked = false

                mdInput.updateValue("")
            }
        }

        groupIdSelect.addEventListener<Event>("change") {
            val newId = groupIdSelect.value.toInt()

            groupNameInput.disabled = newId == -1
            groupPriorityInput.disabled = newId == -1
            groupSaveButton.disabled = newId == -1
            groupDeleteButton.disabled = newId < 0

            if (newId >= 0) {
                pageGroupInputH.updateValue(newId.toString())
                groupNameInput.updateValue(groupIdSelect.selectedOptions.item(0)!!.textContent ?: "")
                GlobalScope.launch {
                    val data = httpClient.get("/admin/group/$newId/metadata").body<EditorGroupMetadataPayload>()
                    groupPriorityInput.updateValue(data.priority.toString())
                }
            } else {
                pageGroupInputH.updateValue("")
                groupNameInput.updateValue("")
                groupPriorityInput.updateValue("0")
            }
        }

        pagePreviewButton.addEventListener<MouseEvent>("click") {
            window.open("/admin/wiki/${pageIdInputH.value}/preview", "_blank")
        }

        imageIdSelect.addEventListener<Event>("change") {
            val imageId = imageIdSelect.value.toInt()

            imageFileInput.disabled = imageId == -1
            imageNameInput.disabled = imageId == -1
            imageSaveButton.disabled = imageId == -1
            imageDeleteButton.disabled = imageId < 0

            if (imageId >= 0) {
                imageNameInput.value = imageIdSelect.selectedOptions.item(0)!!.textContent ?: ""
                val imagePath = "/static/img/${imageNameInput.value.toUrlString()}"
                imagePathPreview.updateValue(imagePath)
                imagePreview.classList.remove("d-none")
                imagePreview.src = imagePath
            } else {
                imageNameInput.updateValue("")
                imagePathPreview.updateValue("")
                imagePreview.classList.add("d-none")
                imagePreview.src = ""
            }
        }

        imageNameInput.addInputListener {
            imagePathPreview.updateValue("/static/img/${imageNameInput.value.toUrlString()}")
        }

        imageFileInput.addEventListener<Event>("change") {
            val file = imageFileInput.files?.item(0) ?: return@addEventListener
            val url = URL.createObjectURL(file)
            imagePreview.classList.remove("d-none")
            imagePreview.src = url
            imagePreview.addEventListener<Event>("load") {
                URL.revokeObjectURL(url)
            }
        }

        val highlight = inactiveTrigger(500) {
            GlobalScope.launch {
                hljs.highlightElement(mdCode)

                mdCode.querySelectorAll<HTMLSpanElement>(".hljs-code") {
                    if (innerText.startsWith("```")) {
                        val lang = innerText.substringAfter("```").substringBefore("\n")
                        GlobalScope.launch {
                            innerHTML = "```$lang\n${hljs.highlight(innerText.substringAfter("```$lang\n").substringBeforeLast("\n```"), json("language" to lang)).value}\n```"
                        }
                    }
                }
            }
        }

        mdInput.addInputListener {
            pageContentInputH.updateValue(mdInput.value)
            mdCode.innerHTML = mdInput.value
            highlight()
        }

        mdInput.addEventListener<Event>("scroll") {
            mdCode.scrollTop = mdInput.scrollTop
            mdCode.scrollLeft = mdInput.scrollLeft
        }

        mdInput.addEventListener<KeyboardEvent>("keydown") {
            if (it.key == "Tab") {
                val selectStart = mdInput.selectionStart!!
                val selectEnd = mdInput.selectionEnd!!

                var beforeSelection = mdInput.value.substring(0, selectStart)
                var selection = mdInput.value.substring(selectStart, selectEnd)
                var afterSelection = mdInput.value.substring(selectEnd)

                if (!(beforeSelection.endsWith("\n") || selection.startsWith("\n"))) {
                    val new = beforeSelection.substringBeforeLast('\n')
                    selection = beforeSelection.substringAfterLast('\n') + selection
                    beforeSelection = new + '\n'
                }
                if (!selection.endsWith("\n")) {
                    val new = afterSelection.substringAfter('\n')
                    selection += afterSelection.substringBefore('\n') + '\n'
                    afterSelection = new
                }

                val newSelection = if (it.shiftKey) {
                    val len = selection.substringBefore('\n').length
                    var new = selection.trimIndent()
                    var deIndentSize = len - new.substringBefore('\n').length
                    if (deIndentSize > 4) {
                        deIndentSize = 4
                        new = new.prependIndent(" ".repeat(deIndentSize - 4))
                    }
                    mdInput.selectionStart = selectStart - deIndentSize
                    mdInput.selectionEnd = selectEnd + new.length - selection.length
                    new
                } else {
                    val new = selection.prependIndent("    ")
                    mdInput.selectionStart = selectStart + 4
                    mdInput.selectionEnd = selectEnd + new.length - selection.length
                    new
                }
                mdInput.updateValue(beforeSelection + newSelection + afterSelection)
            }
        }
    }

//    val wikiSelect = document.querySelector<HTMLSelectElement>("#wjs-wiki-id") {
//        onchange = {
//            val (wikiId, groupId) = value.split(":")
//
//            document.dispatchEvent(CustomEvent("wiki:wiki_id_change", jso {
//                detail = Pair(wikiId, groupId)
//            }))
//        }
//
//        window.localStorage["wiki:wiki_id"]?.let { id ->
//            document.addEventListener<Event>("DOMContentLoaded") { _ ->
//                updateValue(this.options.asList().filterIsInstance<HTMLOptionElement>().first { it.value.split(":").first() == id }.text)
//            }
//        }
//    }
//
//    val groupSelect = document.querySelector<HTMLSelectElement>("#wjs-group-id") {
//        onchange = {
//            document.dispatchEvent(CustomEvent("wiki:group_id_change", jso {
//                detail = value
//            }))
//        }
//    }
//
//    val groupName = document.querySelector<HTMLInputElement>("#wjs-group-name")
//    val groupPriority = document.querySelector<HTMLInputElement>("#wjs-group-priority")
//    val pageUrl = document.querySelector<HTMLInputElement>("#wjs-wiki-url")
//    val pageTitle = document.querySelector<HTMLInputElement>("#wjs-wiki-title") {
//        oninput = {
//            pageUrl!!.updateValue(value.toUrlString())
//        }
//    }
//    val wikiDescription = document.querySelector<HTMLInputElement>("#wjs-wiki-description")
//    val wikiPublic = document.querySelector<HTMLInputElement>("#wjs-wiki-public")
//    val wikiPriority = document.querySelector<HTMLInputElement>("#wjs-wiki-priority")
//
//    val saveWikiBtn = document.querySelector<HTMLInputElement>("#wjs-wiki-save") {
//        onsubmit = {
//            it.preventDefault()
//
//            val form = document.querySelector<HTMLFormElement>("#wjs-form-wiki")!!
//            if (form.checkValidity()) {
//                GlobalScope.launch {
//                    disabled = true
//                    form.submit()
//
//                    val wikiId = document.querySelector<HTMLInputElement>("#wjs-wiki-id")!!.value.split(":").first()
//                    if (wikiId == "-2") {
//                        window.location.reload()
//                    } else {
//                        disabled = false
//                    }
//                }
//            }
//        }
//    }
//
//    val deleteWikiBtn = document.querySelector<HTMLInputElement>("#wjs-wiki-delete") {
//        onsubmit = {
//            it.preventDefault()
//            val form = document.querySelector<HTMLFormElement>("#wjs-form-wiki")!!
//            if (form.checkValidity()) {
//                document.querySelector<HTMLInputElement>("#wjs-wiki-delete-check")?.checked = true
//                saveWikiBtn!!.click()
//            }
//        }
//    }
//
//    val previewBtn = document.querySelector<HTMLButtonElement>("#wjs-wiki-preview") {
//        var id = ""
//
//        document.addEventListener<CustomEvent>("wiki:wiki_id_change") {
//            val (wikiId, _, _) = it.detail as Triple<String, String, String>
//            id = wikiId
//        }
//
//        onclick = {
//            window.open("/admin/wiki/$id/preview", "_blank")
//        }
//    }
//
//    val pageId = document.querySelector<HTMLInputElement>("#wjs-page-id")
//
//    val wikiContent = document.querySelector<HTMLTextAreaElement>("#wjs-wiki-content") {
//        val code = document.querySelector("#wjs-wiki-code")!!
//        val out = document.querySelector<HTMLTextAreaElement>("#wjs-wiki-content-out")!!
//
//        val trigger = inactiveTrigger(500) {
//            GlobalScope.launch {
//                hljs.highlightElement(code)
//
//                code.querySelectorAll<HTMLSpanElement>(".hljs-code") {
//                    if (innerText.startsWith("```")) {
//                        val lang = innerText.substringAfter("```").substringBefore("\n")
//                        GlobalScope.launch {
//                            innerHTML = "```$lang\n${hljs.highlight(innerText.substringAfter("```$lang\n").substringBeforeLast("\n```"), json("language" to lang)).value}\n```"
//                        }
//                    }
//                }
//            }
//        }
//
//        oninput = {
//            code.innerHTML = value.escapeHTML()
//            out.value = value
//            trigger()
//        }
//        onchange = {
//            code.innerHTML = value.escapeHTML()
//            out.value = value
//            trigger()
//        }
//        onscroll = {
//            code.scrollTop = this.scrollTop
//            code.scrollLeft = this.scrollLeft
//            Unit
//        }
//
//        addEventListener<KeyboardEvent>("keydown") {
//            if (it.key == "Tab") {
//                it.preventDefault()
//                val start = selectionStart!!
//                val end = selectionEnd!!
//                val before = value.substring(0, start)
//                val lines = before.split("\n").toMutableList()
//                val lastLine = lines.last()
//
//                if (it.shiftKey) {
//                    // dedent
//                    if (lastLine.startsWith("    ")) {
//                        lines[lines.lastIndex] = lastLine.substring(4)
//                        updateValue(lines.joinToString("\n") + value.substring(start))
//                        selectionStart = start - 4
//                        selectionEnd = end - 4
//                    }
//                } else {
//                    // indent
//                    lines[lines.lastIndex] = "    $lastLine"
//                    updateValue(lines.joinToString("\n") + value.substring(start))
//                    selectionStart = start + 4
//                    selectionEnd = end + 4
//                }
//            }
//        }
//
//        document.addEventListener<CustomEvent>("wiki:wiki_id_change") {
//            val (wikiId, _) = it.detail as Triple<String, String, String>
//
//            if (wikiId != "-2") {
//                GlobalScope.launch {
//                    val res = httpClient.get("/admin/wiki/$wikiId/markdown")
//                    this@querySelector.updateValue(res.bodyAsText())
//                }
//            } else {
//                this@querySelector.updateValue("")
//            }
//        }
//    }
//
//    document.addEventListener<CustomEvent>("wiki:wiki_id_change") {
//        val (wikiId, groupId) = it.detail as Pair<String, String>
//
//        window.localStorage["wiki:wiki_id"] = wikiId
//
//        groupSelect!!.disabled = false
//        pageTitle!!.disabled = false
//        wikiDescription!!.disabled = false
//        wikiPublic!!.disabled = false
//        wikiPriority!!.disabled = false
//        saveWikiBtn!!.disabled = false
//        deleteWikiBtn!!.disabled = wikiId == "-2"
//        previewBtn!!.disabled = wikiId == "-2"
//        pageId!!.disabled = false
//        wikiContent!!.disabled = false
//
//        groupSelect.updateValue(groupId)
//        pageTitle.updateValue(wikiSelect!!.options.asList().filterIsInstance<HTMLOptionElement>().first { it.value.split(":").first() == wikiId }.text)
//        if (deleteWikiBtn.disabled) {
//            deleteWikiBtn.classList.add("d-none")
//        } else {
//            deleteWikiBtn.classList.remove("d-none")
//        }
//        pageId.updateValue(wikiId)
//
//        if (wikiId != "-2") {
//            GlobalScope.launch {
//                val res = httpClient.get("/admin/wiki/$wikiId/metadata")
//                val metadata = res.body<EditorMetadataPayload>()
//                wikiDescription.updateValue(metadata.description)
//                wikiPublic.checked = metadata.published
//                wikiPriority.updateValue(metadata.priority.toString())
//            }
//        } else {
//            wikiDescription.updateValue("")
//            wikiPublic.checked = false
//            wikiPriority.updateValue("0")
//        }
//    }
//
//    document.addEventListener<CustomEvent>("wiki:group_id_change") {
//        val groupId = it.detail as String
//
//        groupName!!.disabled = false
//        groupPriority!!.disabled = false
//
//        if (groupId != "-2") {
//            groupName.updateValue(groupSelect!!.options.asList().filterIsInstance<HTMLOptionElement>().first { it.value == groupId }.text)
//            GlobalScope.launch {
//                val res = httpClient.get("/admin/group/$groupId/metadata")
//                val metadata = res.body<EditorGroupMetadataPayload>()
//                groupPriority.updateValue(metadata.priority.toString())
//            }
//        } else {
//            groupName.updateValue("")
//            groupPriority.updateValue("0")
//        }
//    }
}
