package com.martmists.wiki.backend.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object WikiImageTable : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
    val data = blob("data")
}