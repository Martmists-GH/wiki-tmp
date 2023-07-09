package com.martmists.wiki.backend.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object WikiPageTable : IntIdTable() {
    val title = varchar("title", 255).uniqueIndex()
    val titleUrl = varchar("title_url", 255).uniqueIndex()
    val content = text("content")
    val published = bool("published")
    val category = reference("category", WikiCategoryTable).nullable()
}
