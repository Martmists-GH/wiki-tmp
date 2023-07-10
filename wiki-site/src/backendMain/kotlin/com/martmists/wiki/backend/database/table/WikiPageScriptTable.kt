package com.martmists.wiki.backend.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object WikiPageScriptTable : IntIdTable() {
    val page = reference("page", WikiPageTable)
    val script = blob("source")
    val compiled = blob("compiled")
}
