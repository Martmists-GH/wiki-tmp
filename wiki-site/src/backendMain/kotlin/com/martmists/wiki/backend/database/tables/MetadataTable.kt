package com.martmists.wiki.backend.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object MetadataTable : IntIdTable() {
    private val key = varchar("key", 32).uniqueIndex()
    private val value = varchar("value", 512)
}
