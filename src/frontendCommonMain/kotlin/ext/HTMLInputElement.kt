@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER", "UNCHECKED_CAST")

package ext

import org.w3c.dom.*

fun HTMLInputElement.updateValue(value: String) {
    this.value = value
    this.dispatchEvent(CustomEvent("change"))
}
fun HTMLSelectElement.updateValue(value: String) {
    this.value = value
    this.dispatchEvent(CustomEvent("change"))
}
fun HTMLTextAreaElement.updateValue(value: String) {
    this.value = value
    this.dispatchEvent(CustomEvent("change"))
}
