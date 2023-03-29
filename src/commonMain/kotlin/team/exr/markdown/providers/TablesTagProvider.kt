package team.exr.markdown.providers

import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.lexer.Compat.assert

class TablesTagProvider : GeneratingProvider {
    override fun processNode(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
        assert(node.type == GFMElementTypes.TABLE)

        val alignmentInfo = getAlignmentInfo(text, node)
        var rowsPopulated = 0

        visitor.consumeTagOpen(node, "table", "class=\"table table-bordered table-hover\"")
        for (child in node.children) {
            if (child.type == GFMElementTypes.HEADER) {
                visitor.consumeHtml("<thead>")
                populateRow(visitor, child, "th", alignmentInfo, -1)
                visitor.consumeHtml("</thead>")
            } else if (child.type == GFMElementTypes.ROW) {
                if (rowsPopulated == 0) {
                    visitor.consumeHtml("<tbody>")
                }
                rowsPopulated++
                populateRow(visitor, child, "td", alignmentInfo, rowsPopulated)
            }
        }
        if (rowsPopulated > 0) {
            visitor.consumeHtml("</tbody>")
        }
        visitor.consumeTagClose("table")
    }

    private fun populateRow(visitor: HtmlGenerator.HtmlGeneratingVisitor,
                            node: ASTNode,
                            cellName: String,
                            alignmentInfo: List<Alignment>,
                            rowNumber: Int) {
        val parityAttribute = if (rowNumber > 0 && rowNumber % 2 == 0) "class=\"intellij-row-even\"" else null

        visitor.consumeTagOpen(node, "tr", parityAttribute)
        for (child in node.children.filter { it.type == GFMTokenTypes.CELL }.withIndex()) {
            if (child.index >= alignmentInfo.size) {
                throw IllegalStateException("Too many cells in a row! Should check parser.")
            }

            val alignment = alignmentInfo[child.index]
            val alignmentAttribute = if (alignment.isDefault) null else "align=\"${alignment.htmlName}\""

            visitor.consumeTagOpen(child.value, cellName, alignmentAttribute)
            visitor.visitNode(child.value)
            visitor.consumeTagClose(cellName)
        }

        for (i in node.children.count { it.type == GFMTokenTypes.CELL }..alignmentInfo.size - 1) {
            visitor.consumeHtml("<td></td>")
        }
        visitor.consumeTagClose("tr")
    }

    private fun getAlignmentInfo(text: String, node: ASTNode): List<Alignment> {
        val separatorRow = node.findChildOfType(GFMTokenTypes.TABLE_SEPARATOR)
                ?: throw IllegalStateException("Could not find table separator")

        val result = ArrayList<Alignment>()

        val cells = SPLIT_REGEX.split(separatorRow.getTextInNode(text))
        for (i in cells.indices) {
            val cell = cells[i]
            if (!cell.isBlank() || i in 1..cells.lastIndex - 1) {
                val trimmed = cell.trim()
                val starts = trimmed.startsWith(':')
                val ends = trimmed.endsWith(':')
                result.add(if (starts && ends) {
                    Alignment.CENTER
                } else if (starts) {
                    Alignment.LEFT
                } else if (ends) {
                    Alignment.RIGHT
                } else {
                    DEFAULT_ALIGNMENT
                })
            }
        }
        return result
    }

    enum class Alignment(val htmlName: String, val isDefault: Boolean) {
        LEFT("left", true),
        CENTER("center", false),
        RIGHT("right", false)
    }

    companion object {
        val DEFAULT_ALIGNMENT = Alignment.values().find { it.isDefault }
                ?: throw IllegalStateException("Must me default alignment")

        val SPLIT_REGEX = Regex("\\|")
    }
}
