package team.exr.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object WikiImageTable : IntIdTable() {
    val name = varchar("name", 64).uniqueIndex()
    val image = binary("image", 16777216)  // 16MB max
}
