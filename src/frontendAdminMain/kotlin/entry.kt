import editor.`editor$elements`
import kotlinx.browser.window
import org.w3c.dom.CustomEvent
import ext.addEventListener
import hljs.`hljs$patchMarkdown`
import io.ktor.client.*
import io.ktor.client.engine.js.*

val httpClient = HttpClient(Js)

suspend fun main() {
    window.addEventListener<CustomEvent>("wiki:page_loaded") {
        `hljs$patchMarkdown`()
        `editor$elements`()
    }
}
