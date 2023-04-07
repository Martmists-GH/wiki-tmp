@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER", "UNCHECKED_CAST")

package ext

import org.w3c.dom.Element
import org.w3c.dom.ParentNode
import org.w3c.dom.asList

fun <T : Element> ParentNode.querySelector(selector: String): T? = querySelector(selector) as? T
fun <T : Element> ParentNode.querySelector(selector: String, block: T.() -> Unit): T? = querySelector<T>(selector)?.also(block)
fun <T : Element> ParentNode.querySelector(selector: String, block: T.() -> Any?): T? = querySelector<T>(selector)?.also { it.block() }
inline fun <reified T : Element> ParentNode.querySelectorAll(selector: String): List<T> = querySelectorAll(selector).asList().filterIsInstance<T>()
inline fun <reified T : Element> ParentNode.querySelectorAll(selector: String, crossinline block: T.() -> Unit): List<T> = querySelectorAll<T>(selector).onEach(block)
inline fun <reified T : Element> ParentNode.querySelectorAll(selector: String, crossinline block: T.() -> Any?): List<T> = querySelectorAll<T>(selector).onEach { it.block() }
