package team.exr.database

import com.fasterxml.jackson.annotation.JsonProperty

enum class DatabaseDriver {
    @JsonProperty("h2")
    H2,
    @JsonProperty("sqlite")
    SQLITE,
    @JsonProperty("postgres")
    POSTGRES,
}
