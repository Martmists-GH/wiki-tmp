import halfmoon.`halfmoon$buttons`
import ext.addEventListener
import kotlinx.browser.window
import org.w3c.dom.CustomEvent
import sidebar.`sidebar$scrollSmooth`

suspend fun main() {
    window.addEventListener<CustomEvent>("wiki:page_loaded") {
        `halfmoon$buttons`()
        `sidebar$scrollSmooth`()

        hljs.highlightAll()
    }
}
