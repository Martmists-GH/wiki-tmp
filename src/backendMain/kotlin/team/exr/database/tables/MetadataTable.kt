package team.exr.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object MetadataTable : IntIdTable() {
    private val key = varchar("key", 32).uniqueIndex()
    private val value = varchar("value", 512)

    context(Transaction)
    fun get(key: String): String {
        return select { MetadataTable.key eq key }.first()[value]
    }

    context(Transaction)
    fun getOrDefault(key: String, default: String): String {
        return select { MetadataTable.key eq key }.firstOrNull()?.get(value) ?: default
    }

    context(Transaction)
    fun getOrPut(key: String, default: () -> String): String {
        return select { MetadataTable.key eq key }.firstOrNull()?.get(value) ?: default().also {
            set(key, it)
        }
    }

    context(Transaction)
    fun set(key: String, value: String) {
        insert {
            it[MetadataTable.key] = key
            it[MetadataTable.value] = value
        }
    }
}
