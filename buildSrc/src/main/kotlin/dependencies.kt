import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun DependencyHandlerScope.ktor(module: String) = "io.ktor:ktor-$module-jvm:${Versions.ktor}"
fun DependencyHandlerScope.ktorClient(module: String) = ktor("client-$module")
fun DependencyHandlerScope.ktorServer(module: String) = ktor("server-$module")

fun KotlinDependencyHandler.ktor(module: String) = "io.ktor:ktor-$module-jvm:${Versions.ktor}"
fun KotlinDependencyHandler.ktorClient(module: String) = ktor("client-$module")
fun KotlinDependencyHandler.ktorServer(module: String) = ktor("server-$module")
