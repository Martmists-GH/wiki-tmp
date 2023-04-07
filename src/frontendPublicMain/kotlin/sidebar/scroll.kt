package sidebar

import ext.addEventListener
import ext.querySelector
import ext.querySelectorAll
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.events.MouseEvent

fun `sidebar$scrollSmooth`() {
    document.querySelectorAll<HTMLAnchorElement>("a[href^=\"#\"]").forEach { anchor ->
        anchor.addEventListener<MouseEvent>("click") { e ->
            println("Clicked!")
            e.preventDefault()
            val item = document.querySelector<HTMLHeadingElement>(anchor.getAttribute("href")!!)!!
            item.scrollIntoView(js("{behavior: 'smooth'}"))
        }
    }
}
