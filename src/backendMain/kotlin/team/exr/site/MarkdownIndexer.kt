package team.exr.site

import com.vladsch.flexmark.ext.footnotes.FootnoteExtension
import com.vladsch.flexmark.ext.gitlab.GitLabExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import team.exr.database.DatabaseHandler
import team.exr.database.tables.WikiCategoryTable
import team.exr.database.tables.WikiPageTable
import team.exr.ext.*
import team.exr.markdown.MarkdownParsingContext
import team.exr.markdown.OnThisPageCollector
import team.exr.markdown.WikiExtension


object MarkdownIndexer {
    data class Group(val name: String, val index: Int, val pages: List<Page>)
    data class Page(val id: Int, val name: String, val description: String, val public: Boolean, val index: Int, val context: MarkdownParsingContext) {
        fun anchorMap(): List<Pair<String, String>> {
            return context.links.map { Pair(it, it.toUrlString()) }
        }
    }

    private val groups = mutableListOf<Group>()

    private val markdownOptions = MutableDataSet().apply {
        set(HtmlRenderer.INDENT_SIZE, 2)
        set(HtmlRenderer.PERCENT_ENCODE_URLS, true)
        set(Parser.EXTENSIONS, listOf(
            FootnoteExtension.create(),
            GitLabExtension.create(),
            TablesExtension.create(),
            WikiExtension,
        ))

        set(HtmlRenderer.FENCED_CODE_NO_LANGUAGE_CLASS, "code")
        set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "code language-")
    }
    private val markdownParser = Parser.builder(markdownOptions).build()
    private val markdownRenderer = HtmlRenderer.builder(markdownOptions).build()

    fun getSidebarEntries(public: Boolean): List<Group> {
        return groups.filter { it.pages.any { it.public || !public } && it.name != "index" }
    }

    suspend fun pageFor(id: Int): Page? {
        val (groupName, pageName) = DatabaseHandler.transactionAsync {
            val row = WikiPageTable.select { WikiPageTable.id eq id }.firstOrNull() ?: return@transactionAsync null
            val groupRow = WikiCategoryTable.select { WikiCategoryTable.id eq row[WikiPageTable.category] }.first()
            row[WikiPageTable.name] to groupRow[WikiCategoryTable.name]
        } ?: return null
        return pageFor("$groupName/$pageName")
    }

    fun pageFor(path: String): Page? {
        if (path == "index") {
            return groups.firstOrNull { it.name == "index" }?.pages?.firstOrNull()
        }

        val (groupName, pageName) = path.split('/')
        val group = groups.firstOrNull { it.name.toUrlString() == groupName } ?: return null
        return group.pages.firstOrNull { it.name.toUrlString() == pageName }
    }

    suspend fun render(page: Page): String {
        val content = DatabaseHandler.transactionAsync {
            val row = WikiPageTable.select { WikiPageTable.id eq page.id }.first()
            row[WikiPageTable.content]
        }

        val document = markdownParser.parse(content)
        return markdownRenderer.render(document)
    }

    suspend fun rebuildIndex() {
        DatabaseHandler.transactionAsync {
            val allGroups = WikiCategoryTable.selectAll().orderBy(WikiCategoryTable.name).map {
                val pages = WikiPageTable.selectAll().orderBy(WikiPageTable.name).map {
                    val ctx = MarkdownParsingContext()
                    val document = markdownParser.parse(it[WikiPageTable.content])
                    ctx.links.addAll(OnThisPageCollector.collect(document))

                    Page(
                        it[WikiPageTable.id].value,
                        it[WikiPageTable.name],
                        it[WikiPageTable.description],
                        it[WikiPageTable.published],
                        it[WikiPageTable.priority],
                        ctx,
                    )
                }

                Group(it[WikiCategoryTable.name], it[WikiCategoryTable.priority], pages.sortedBy { -it.index })
            }

            groups.clear()
            groups.addAll(allGroups.sortedBy { -it.index })
        }
    }
}
