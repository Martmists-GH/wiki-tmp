package com.martmists.wiki.backend.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object MetadataTable : IntIdTable() {
    val key = varchar("key", 32).uniqueIndex()
    val value = varchar("value", 512)
}
