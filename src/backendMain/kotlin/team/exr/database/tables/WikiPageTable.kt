package team.exr.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object WikiPageTable : IntIdTable() {
    val category = reference("category", WikiCategoryTable.id)
    val name = varchar("name", 64)
    val content = text("content")
    val description = varchar("description", 256)
    val published = bool("published").default(false)

    init {
        uniqueIndex(category, name)
    }
}
