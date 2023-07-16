package com.martmists.wiki.backend.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object AdminTable : IntIdTable() {
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 512)
}
