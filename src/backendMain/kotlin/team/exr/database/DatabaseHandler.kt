package team.exr.database

import com.martmists.commons.database.ThreadedDatabase
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import team.exr.config.ConfigLoader
import team.exr.database.tables.*
import java.sql.Connection

private fun loadDatabase(): Database {
    val config = ConfigLoader.default.database

    return when (config.driver) {
        DatabaseDriver.H2 -> {
            Database.connect(
                "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;",
                driver = "org.h2.Driver"
            )
        }
        DatabaseDriver.SQLITE -> {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            Database.connect(
                "jdbc:sqlite:${config.database}",
                driver = "org.sqlite.JDBC"
            )
        }
        DatabaseDriver.POSTGRES -> {
            Database.connect(
                "jdbc:postgresql://${config.host}:${config.port}/${config.database}",
                driver = "org.postgresql.Driver",
                user = config.username,
                password = config.password
            )
        }
    }
}

object DatabaseHandler : ThreadedDatabase(createDb = ::loadDatabase) {
   suspend fun load() {
        transactionAsync {
            SchemaUtils.createMissingTablesAndColumns(
                MetadataTable,
                UserTable,
                WikiCategoryTable,
                WikiPageTable,
                WikiImageTable,
            )
        }
    }

    suspend fun <T> transactionAsync(block: suspend Transaction.() -> T) : T {
        val future = transaction {
            runBlocking {
                block()
            }
        }

        return future.await()
    }
}
