package team.exr.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object WikiCategoryTable : IntIdTable() {
    val name = varchar("name", 64).uniqueIndex()
    val priority = integer("priority")
}
