package com.martmists.wiki.backend.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun CoroutineScope.spawnTransaction(block: suspend () -> Unit) {
    launch {
        newSuspendedTransaction {
            block()
        }
    }
}
