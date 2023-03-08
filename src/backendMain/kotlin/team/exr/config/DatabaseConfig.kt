package team.exr.config

import team.exr.database.DatabaseDriver

data class DatabaseConfig(
    val driver: DatabaseDriver,
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
)
