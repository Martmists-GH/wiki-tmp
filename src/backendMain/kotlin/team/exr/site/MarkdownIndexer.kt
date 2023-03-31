package team.exr.site

import team.exr.ext.*
import team.exr.markdown.MarkdownParsingContext
import team.exr.markdown.MarkdownTransformer
import java.io.InputStream
import kotlin.io.path.listDirectoryEntries


object MarkdownIndexer {
    data class Group(val name: String, val index: Int, val pages: List<Page>)
    data class Page(val name: String, val path: String, val filePath: String, val index: Int, val context: MarkdownParsingContext) {
        fun anchorMap(): List<Pair<String, String>> {
            return context.links.map { Pair(it, it.toUrlString()) }
        }
    }
    private val groups = mutableListOf<Group>()
    private lateinit var rootGroup: Group


    fun index() {
        fun collectFile(dirname: String, file: String, pages: MutableList<Page>) {
            val filePath = if (dirname.isEmpty()) "/wiki/$file" else "/wiki/$dirname/$file"
            val stream = getResourceAsStream(filePath)
            val markdown = stream.readAllBytes().decodeToString()

            val ctx = MarkdownParsingContext()
            MarkdownTransformer.handle(markdown, ctx)

            val numPrefix = Regex("(\\d+_).+").matchEntire(file)?.groupValues?.get(1) ?: ""

            pages.add(Page(
                file.removeSurrounding(numPrefix, ".md").toHumanString(),
                "/wiki/${dirname.replace(Regex("^\\d+_"), "")}/${file.removeSurrounding(numPrefix, ".md")}",
                filePath,
                numPrefix.removeSuffix("_").toIntOrNull() ?: 0,
                ctx,
            ))
        }

        fun collectFilesInDir(dirname: String) {
            val pages = mutableListOf<Page>()
            for (file in getResources("/wiki/$dirname")) {
                if (file.endsWith(".md")) {
                    collectFile(dirname, file, pages)
                }
            }

            val numPrefix = Regex("(\\d+_).+").matchEntire(dirname)?.groupValues?.get(1) ?: ""
            val idx = (numPrefix.removeSuffix("_").toIntOrNull() ?: 0)

            groups.add(Group(dirname.removePrefix(numPrefix).toHumanString(), idx, pages.sortedBy { it.index }))
        }

        groups.clear()
        for (dirname in getResources("/wiki/")) {
            if (!dirname.endsWith(".md")) {
                collectFilesInDir(dirname)
            }
        }

        val p = mutableListOf<Page>()
        for (file in getResources("/wiki/")) {
            if (file.endsWith(".md")) {
                collectFile("", file, p)
            }
        }
        rootGroup = Group("Root", -1, p.sortedBy { it.index })
    }

    fun getSidebarEntries(): List<Group> {
        return groups.sortedBy { it.index }
    }

    fun getPageFor(path: String) : Page {
        if (path == "/wiki/index") {
            return rootGroup.pages.first()
        }

        for (group in groups) {
            for (page in group.pages) {
                if (page.path == path) {
                    return page
                }
            }
        }

        throw IllegalArgumentException("No page found for $path")
    }

    fun load(stream: InputStream) : String {
        val markdown = stream.readAllBytes().decodeToString()
        return MarkdownTransformer.handle(markdown)
    }

    private fun getResources(path: String): List<String> {
        val filenames = mutableListOf<String>()
        getResource(path).toURI().getPath {
            filenames.addAll(it.listDirectoryEntries().map { p -> p.toString().replace('\\', '/').split(path).last().trimStart('/') })
        }
        return filenames
    }
}
