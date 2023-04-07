package ext

import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

fun EventTarget.addEventListener(event: String, block: () -> Any?) = addEventListener<Event>(event) { block() }
fun EventTarget.addEventListener(event: String, block: (Event) -> Any?) = addEventListener<Event>(event, block)

@Suppress("UNCHECKED_CAST")
fun <T : Event> EventTarget.addEventListener(event: String, block: (T) -> Any?) {
    addEventListener(event, { (it as? T)?.let(block) })
}
fun <T : Event> EventTarget.addEventListener(event: String, block: (T) -> Unit) {
    addEventListener(event, { (it as? T)?.let(block) })
}

fun inactiveTrigger(timeout: Int = 1000, block: () -> Unit): () -> Unit {
    var timer = 0;
    return {
        window.clearTimeout(timer)
        timer = window.setTimeout(block, timeout)
    }
}
