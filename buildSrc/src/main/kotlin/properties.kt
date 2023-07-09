import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

val Project.production: Boolean
    get() {
        val production: String? by project
        return (production ?: "false").toBoolean()
    }
