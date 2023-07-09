package com.martmists.wiki.backend.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object AdminTable : IntIdTable() {
    val username = varchar("username", 255).uniqueIndex()
    val password = binary("password", 512)
}
