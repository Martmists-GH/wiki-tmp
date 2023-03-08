import editor.`editor$transformer`
import halfmoon.`halfmoon$buttons`
import ext.addEventListener
import hljs.`hljs$patchMarkdown`
import kotlinx.browser.window
import org.w3c.dom.CustomEvent
import sidebar.`sidebar$scrollSmooth`

suspend fun main() {
    window.addEventListener<CustomEvent>("exr:page_loaded") {
        `hljs$patchMarkdown`()
        `halfmoon$buttons`()
        `editor$transformer`()
        `sidebar$scrollSmooth`()

        hljs.highlightAll()
    }
}
