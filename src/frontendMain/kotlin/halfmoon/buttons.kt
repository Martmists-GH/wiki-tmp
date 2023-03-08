package halfmoon

import ext.addEventListener
import ext.querySelector
import ext.querySelectorAll
import halfmoon
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.MouseEvent

fun `halfmoon$buttons`() {
    // Sidebar hidden overlay
    document.querySelector<HTMLDivElement>(".sidebar-overlay")?.addEventListener<MouseEvent>("click") {
        halfmoon.toggleSidebar()
    }

    // Navbar buttons
    document.querySelector<HTMLButtonElement>(".btn-toggle-sidebar")?.addEventListener<MouseEvent>("click") {
        halfmoon.toggleSidebar()
    }
    document.querySelector<HTMLButtonElement>(".btn-toggle-dark")?.addEventListener<MouseEvent>("click") {
        halfmoon.toggleDarkMode()
    }
}
