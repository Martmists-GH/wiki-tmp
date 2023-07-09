package com.martmists.wiki.backend.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object WikiCategoryTable : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
    val nameUrl = varchar("name_url", 255).uniqueIndex()
}
