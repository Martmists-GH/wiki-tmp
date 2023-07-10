import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.martmists.wiki.frontend.fontawesome.FaIcon
import com.martmists.wiki.frontend.halfmoon.component.*
import com.martmists.wiki.frontend.halfmoon.enum.HmBreakpoint
import com.martmists.wiki.frontend.halfmoon.enum.HmContainerSize
import com.martmists.wiki.frontend.halfmoon.halfmoon
import com.martmists.wiki.frontend.halfmoon.halfmoonOnDOMContentLoaded
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposableInBody

fun main() {
    document.body?.apply {
        classList.add(
            "dark-mode",
            "with-custom-webkit-scrollbars",
            "with-custom-css-scrollbars",
        )
        setAttribute("data-set-preferred-theme-onload", "true")
    }

    renderComposableInBody {
        Div(attrs = {
            classes("page-wrapper", "with-navbar", "with-sidebar")
            attr("data-sidebar-type", "overlayed-sm-and-down")
        }) {
            HmNavbar {
                HmNavbarContent {
                    HmButton({
                        action()
                        onClick {
                            halfmoon.toggleSidebar()
                        }
                    }) {
                        FaIcon("fa-solid fa-bars")
                        Span({
                            classes("sr-only")
                        }) {
                            Text("Toggle sidebar")
                        }
                    }
                }

                HmNavbarBrand {
                    Text("My Website")
                }

                HmNavbarNavigation({
                    classes("hidden-sm-and-down")
                }) {
                    // TODO
                }

                HmNavbarContent({
                    classes("ml-auto")
                }) {
                    HmButton({
                        action()
                        classes("mr-5")
                        attr("aria-label", "Toggle dark mode")
                        onClick {
                            halfmoon.toggleDarkMode()
                        }
                    }) {
                        FaIcon("fa fa-moon")
                    }
                }
            }

            Div({
                classes("sidebar-overlay")
            })

            HmSidebar {
                HmSidebarTitle {
                    Text("Menu")
                }
                HmSidebarDivider()
                HmSidebarLink("Link 1", "#")
                HmSidebarLink("Link 2", "#")
            }

            Div({
                classes("content-wrapper")
            }) {
                HmContainer(HmContainerSize.FLUID) {
                    HmRow {
                        val page = remember { mutableStateOf("Home") }

                        HmCol(9 to HmBreakpoint.XL) {
                            HmContent {
                                H3 {
                                    Text(page.value)
                                }
                            }
                        }
                        HmCol(3 to HmBreakpoint.XL) {
                            HmContent {
                                Div({
                                    classes("on-this-page-nav")
                                }) {
                                    Div({
                                        classes("title")
                                    }) {
                                        Text("On this page")
                                    }

                                    val options = listOf("Home", "About", "Contact")

                                    options.forEach { opt ->
                                        A(href = "#", attrs = {
                                            onClick {
                                                page.value = opt
                                            }
                                        }) {
                                            Text(opt)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    halfmoonOnDOMContentLoaded()
}
