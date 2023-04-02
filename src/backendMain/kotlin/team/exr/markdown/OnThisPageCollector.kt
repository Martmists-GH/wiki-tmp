package team.exr.markdown

import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.ast.VisitHandler

object OnThisPageCollector {
    fun collect(document: Document): List<String> {
        val out = mutableListOf<String>()

        NodeVisitor(VisitHandler(Heading::class.java) {
            out.add(it.text.toString())
        }).visit(document)

        return out
    }
}
