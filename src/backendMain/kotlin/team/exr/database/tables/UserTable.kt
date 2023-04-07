package team.exr.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable() {
    val name = varchar("name", 32).uniqueIndex()
    // Technically only 420 bytes are needed for our hash, but we'll give it a bit more space
    val passwordHash = varchar("password_hash", 512)
}
