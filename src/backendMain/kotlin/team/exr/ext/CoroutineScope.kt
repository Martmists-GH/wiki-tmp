package team.exr.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Transaction
import team.exr.database.DatabaseHandler

fun CoroutineScope.spawnTransaction(block: Transaction.() -> Unit) {
    launch(Dispatchers.IO) {
        DatabaseHandler.transactionAsync {
            block()
        }
    }
}
