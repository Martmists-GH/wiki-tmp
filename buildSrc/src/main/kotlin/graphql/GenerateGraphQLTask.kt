package graphql

import GraphQLLexer
import GraphQLParser
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.BaseKotlinCompile
import java.io.File
import java.net.URL

abstract class GenerateGraphQLTask : DefaultTask() {
    init {
        group = "generate"
        description = "Generates GraphQL code"
    }

    @get:Input
    @get:Optional
    abstract val schemaUrl: Property<String?>

    @get:Input
    @get:Optional
    abstract val schemaFallback: Property<File>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: Property<File>

    @TaskAction
    fun generate() {
        val schema = schemaUrl.orNull?.let(::loadSchema) ?: schemaFallback.get().readText()
        val output = outputDir.get()

        output.mkdirs()

        val schemaFile = output.resolve("schema.graphql")
        schemaFile.writeText(schema)

        val lexer = GraphQLLexer(ANTLRInputStream(schema))
        val parser = GraphQLParser(CommonTokenStream(lexer))
        val listener = GraphQLGeneratingListener(outputDir.get(), packageName.get(), parser.ruleNames)
        ParseTreeWalker().walk(
            listener,
            parser.graphqlSchema()
        )
    }

    private fun loadSchema(url: String): String? {
        try {
            val connection = URL(url).openConnection()
            connection.connect()
            return connection.getInputStream().use { it.reader().readText() }
        } catch (e: Exception) {
            return null
        }
    }
}
