package com.martmists.wiki.backend

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.martmists.wiki.backend.database.table.MetadataTable
import com.martmists.wiki.backend.ext.hex
import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount
import kotlin.random.Random

object Globals {
    val argon2: Argon2 = Argon2Factory.createAdvanced(32, 256)
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )

    var jwtRealm: String = "wiki"
    private var jwtIssuer: String = "wiki"
    private var jwtAudience: String = "wiki"
    private var jwtSecret: String = Random.nextBytes(64).hex()
    private var jwtValidity: Duration = Duration.of(1, ChronoUnit.HOURS)

    lateinit var jwtVerifier: JWTVerifier
        private set

    fun createJwt(id: String): String {
        return JWT.create()
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .withExpiresAt(Instant.now().plus(jwtValidity))
            .withClaim("id", id)
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    fun init() {
        transaction(database) {
            MetadataTable.selectAll().onEach {
                when (it[MetadataTable.key]) {
                    "jwtRealm" -> jwtRealm = it[MetadataTable.value]
                    "jwtIssuer" -> jwtIssuer = it[MetadataTable.value]
                    "jwtAudience" -> jwtAudience = it[MetadataTable.value]
                    "jwtSecret" -> jwtSecret = it[MetadataTable.value]
                    "jwtValidity" -> jwtValidity = Duration.of(it[MetadataTable.value].toLong(), ChronoUnit.MINUTES)
                }
            }

            MetadataTable.batchInsert(listOf(
                "jwtRealm" to jwtRealm,
                "jwtIssuer" to jwtIssuer,
                "jwtAudience" to jwtAudience,
                "jwtSecret" to jwtSecret,
                "jwtValidity" to jwtValidity.toMinutes().toString()
            ), ignore = true) {
                this[MetadataTable.key] = it.first
                this[MetadataTable.value] = it.second
            }
        }

        jwtVerifier = JWT
            .require(Algorithm.HMAC256(jwtSecret))
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .build()
    }
}
