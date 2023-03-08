@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package ext

import org.w3c.dom.Element
import org.w3c.dom.ParentNode
import org.w3c.dom.asList

@Suppress("UNCHECKED_CAST")
fun <T : Element> ParentNode.querySelector(selector: String): T? = querySelector(selector) as? T
inline fun <reified T : Element> ParentNode.querySelectorAll(selector: String): List<T> = querySelectorAll(selector).asList().filterIsInstance<T>()
