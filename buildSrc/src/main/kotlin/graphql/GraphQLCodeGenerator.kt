package graphql

import java.io.File

class GraphQLCodeGenerator(outputDir: File, private val packageName: String) {
    private val outputDir = outputDir.resolve(packageName.replace('.', '/')).also {
        if (it.exists()) {
            it.deleteRecursively()
        }
        it.mkdirs()
    }

    fun emitCore() {
        val file = outputDir.resolve("core.kt")
        file.writeText(
            """
@file:Suppress("UNCHECKED_CAST")

package $packageName

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

typealias ID = String

class Variable<T>(val type: String, val value: T, val serializer: SerializationStrategy<T>) {
    companion object {
        inline fun <reified T> make(type: String, value: T): Variable<T> {
            return Variable(type, value, serializer())
        }
    }
}

typealias Variables = Map<String, Variable<*>>

class QueryBuilder {
    private val props = mutableListOf<String>()
    private val scopes = mutableListOf<String>()
    val variables = mutableMapOf<String, Variable<*>>()
    
    fun attr(name: String) {
        props += name
    }
    
    fun attr(name: String, mapping: Map<String, String>) {
        props += "${'$'}name(${'$'}{mapping.entries.joinToString(", ") { "${'$'}{it.key}: ${'$'}{it.value}" }})"
    }
    
    fun scope(name: String, block: QueryBuilder.() -> Unit) {
        val builder = QueryBuilder()
        builder.block()
        require(builder.scopes.isNotEmpty() || builder.props.isNotEmpty()) { "Must specify at least one field" }
        val (document, variables) = builder.build()
        scopes += "${'$'}name \n" + document.prependIndent("  ") + "\n"
        this.variables += variables
    }
    
    fun scope(name: String, mapping: Map<String, String>, block: QueryBuilder.() -> Unit) {
        val builder = QueryBuilder()
        builder.block()
        require(builder.scopes.isNotEmpty() || builder.props.isNotEmpty()) { "Must specify at least one field" }
        val (document, variables) = builder.build()
        scopes += "${'$'}name(${'$'}{mapping.entries.joinToString(", ") { "${'$'}{it.key}: ${'$'}${'$'}{it.key}" }}) \n" + document.prependIndent("  ") + "\n"
        this.variables += variables
    }
    
    fun build(): Pair<String, Variables> {
        return "{\n" + props.joinToString("\n", "\n").prependIndent("  ") + scopes.joinToString("\n", "\n").prependIndent("  ") + "}" to variables
    }
}

class Query<T>(private val type: String, private val document: String, internal val variables: Variables, internal val constructor: (JsonObject?) -> T) {
    fun query(): String {
        if (variables.isEmpty())
            return "${'$'}type \n${'$'}{document.prependIndent("  ")}\n"
        return "${'$'}{type} (${'$'}{variables.entries.joinToString(", ") { "${'$'}${'$'}{it.key}: ${'$'}{it.value.type}" }}) \n${'$'}{document.prependIndent("  ")}\n"
    }
}

class GraphQLClient(private val client: HttpClient, private val url: String, private val configure: JsonBuilder.() -> Unit = {}) {
    private val json = Json {
        configure()
    }

    suspend inline fun <reified T> query(block: QueryDSL.() -> Query<T>): T {
        val query = QueryDSL().block()
        return execute(query)
    }
    
    suspend fun <T> mutation(block: MutationDSL.() -> Query<T>): T {
        val query = MutationDSL().block()
        return execute(query)
    }
    
    suspend fun <T> subscription(block: SubscriptionDSL.() -> Query<T>): T {
        val query = SubscriptionDSL().block()
        return execute(query)
    }
    
    suspend fun <T> execute(query: Query<T>): T {
        val response = client.post(url) {
            header("Content-Type", "application/json")
            setBody(buildJsonObject {
                put("query", query.query())
                put("variables", buildJsonObject {
                    query.variables.forEach { (name, variable) ->
                        put(name, json.encodeToJsonElement(variable.serializer as SerializationStrategy<Any?>, variable.value))
                    }
                })
            })
        }

        val res = response.body<JsonObject>()
        
        val data = res["data"] as JsonObject?
        val errors = res["errors"] as JsonArray?
        if (errors != null) {
            throw Exception("GraphQL Error: ${'$'}{errors.joinToString("\n") { it.toString() }}")
        }

        return query.constructor(data)
    }
}
            """.trim()
        )

        emitQuery(emptyList(), emptyList())
        emitMutation(emptyList(), emptyList())
        emitSubscription(emptyList(), emptyList())
    }

    fun emitType(name: String, properties: List<PropertyDef>, methods: List<MethodDef>) {
        val file = outputDir.resolve("$name.kt")
        file.writeText(
            """
@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION", "UNNECESSARY_SAFE_CALL")
package $packageName

import kotlinx.serialization.*
import kotlinx.serialization.json.*

${generateType(name, properties, methods)}
            """.trim()
        )
    }

    fun emitEnum(name: String, values: List<String>) {
        val file = outputDir.resolve("$name.kt")
        file.writeText(
            """
package $packageName

import kotlinx.serialization.*

@Serializable
enum class $name {
${values.joinToString(",\n").prependIndent("  ")}
}

typealias ${name}Model = $name
            """.trim()
        )
    }

    fun emitQuery(properties: List<PropertyDef>, methods: List<MethodDef>) {
        val file = outputDir.resolve("QueryDSL.kt")
        file.writeText(
            """
@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION", "UNNECESSARY_SAFE_CALL")

package $packageName

import kotlinx.serialization.json.*
        
class QueryDSL {
${properties.joinToString("\n") { generatePropertyQuery("query", it) }.prependIndent("    ")}
${methods.joinToString("\n") { generateMethodQuery("query", it) }.prependIndent("    ")}
}
            """.trim()
        )
    }

    fun emitMutation(properties: List<PropertyDef>, methods: List<MethodDef>) {
        val file = outputDir.resolve("MutationDSL.kt")
        file.writeText(
            """
@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION", "UNNECESSARY_SAFE_CALL")

package $packageName

import kotlinx.serialization.json.*
        
class MutationDSL {
${properties.joinToString("\n") { generatePropertyQuery("mutation", it) }.prependIndent("    ")}
${methods.joinToString("\n") { generateMethodQuery("mutation", it) }.prependIndent("    ")}
}
            """.trim()
        )
    }

    fun emitSubscription(properties: List<PropertyDef>, methods: List<MethodDef>) {
        val file = outputDir.resolve("SubscriptionDSL.kt")
        file.writeText(
            """
@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION", "UNNECESSARY_SAFE_CALL")

package $packageName

import kotlinx.serialization.json.*
        
class SubscriptionDSL {
${properties.joinToString("\n") { generatePropertyQuery("mutation", it) }.prependIndent("    ")}
${methods.joinToString("\n") { generateMethodQuery("mutation", it) }.prependIndent("    ")}
}
            """.trim()
        )
    }

    fun generatePropertyQuery(type: String, prop: PropertyDef): String {
        if (prop.type.isScalar) {
            return """
val ${prop.name}: Query<${remapType(prop.type)}>
    get() {
        val builder = QueryBuilder()
        builder.attr("${prop.name}")
        val (document, variables) = builder.build()
        return Query("$type", document, variables, { it?.get("${prop.name}").let ${constructor(prop.type)} })
    }
            """.trim()
        } else {
            return """
fun ${prop.name}(block: ${typeName(prop.type)}DSL.() -> Unit): Query<${remapType(prop.type)}> {
    val builder = QueryBuilder()
    builder.scope("${prop.name}") {
        ${typeName(prop.type)}DSL(this).block()
    }
    val (document, variables) = builder.build()
    return Query("$type", document, variables, { it?.get("${prop.name}").let ${constructor(prop.type)} })
}
        """.trim()
        }
    }

    fun generateMethodQuery(type: String, meth: MethodDef): String {
        val argsMapped =
            meth.args.joinToString(", ") { "${it.name}: ${argType(it.type)}" + if (it.type.isNullable) " = null" else "" }
        val argsList = meth.args.joinToString(
            ", ",
            "mapOf(",
            ")"
        ) { "\"${it.name}\" to Variable.make(\"${gqlType(it.type)}\", ${it.name})" }
        val argsTypes = meth.args.joinToString(", ", "mapOf(", ")") { "\"${it.name}\" to \"${gqlType(it.type)}\"" }

        if (meth.returnType.isScalar) {
            return """
fun ${meth.name}($argsMapped): Query<${remapType(meth.returnType)}> {
    val builder = QueryBuilder()
    builder.attr("${meth.name}", $argsTypes)
    builder.variables.putAll($argsList)
    val (document, variables) = builder.build()
    return Query("$type", document, variables, { it?.get("${meth.name}").let ${constructor(meth.returnType)} })
}
            """.trim()
        } else {
            return """
fun ${meth.name}($argsMapped, block: ${typeName(meth.returnType)}DSL.() -> Unit): Query<${remapType(meth.returnType)}> {
     val builder = QueryBuilder()
     builder.scope("${meth.name}", $argsTypes) {
         ${typeName(meth.returnType)}DSL(this).block()
     }
     builder.variables.putAll($argsList)
     val (document, variables) = builder.build()
     return Query("$type", document, variables, { it?.get("${meth.name}").let ${constructor(meth.returnType)} })
}
            """.trim()
        }
    }

    fun generateType(name: String, properties: List<PropertyDef>, methods: List<MethodDef>): String {
        val props = properties.joinToString("\n", transform = ::generateProperty)
        val meths = methods.joinToString("\n", transform = ::generateMethod)

        val propsDSL = properties.joinToString("\n", transform = ::generatePropertyDSL)
        val methsDSL = methods.joinToString("\n", transform = ::generateMethodDSL)

        return """
class ${name}(private val json: JsonObject) {
${props.prependIndent("    ")}
${meths.prependIndent("    ")}
}

class ${name}DSL(private val builder: QueryBuilder) {
${propsDSL.prependIndent("    ")}
${methsDSL.prependIndent("    ")}
}

@Serializable
data class ${name}Model(
${properties.joinToString(",\n") { "val ${it.name}: ${argType(it.type)}" + if (it.type.isNullable) " = null" else "" }.prependIndent("    ")}
)
        """.trim()
    }

    fun generateProperty(prop: PropertyDef): String {
        return """
val ${prop.name}: ${remapType(prop.type)} by lazy {
${getElement(prop.name, prop.type).prependIndent("    ")}
}
        """.trim()
    }

    fun generatePropertyDSL(prop: PropertyDef): String {
        if (prop.type.isScalar) {
            return """
val ${prop.name}: Unit
    get() = builder.attr("${prop.name}")
            """.trim()
        }
        return """
fun ${prop.name}(block: ${typeName(prop.type)}DSL.() -> Unit) {
    builder.scope("${prop.name}") {
        ${typeName(prop.type)}DSL(this).block()
    }
}
        """.trim()
    }

    fun generateMethod(meth: MethodDef): String {
        return """
val ${meth.name}: ${remapType(meth.returnType)} by lazy {
${getElement(meth.name, meth.returnType).prependIndent("    ")}
}
        """.trim()
    }

    fun generateMethodDSL(meth: MethodDef): String {
        val argsMapped = meth.args.joinToString(", ") { "${it.name}: ${argType(it.type)}" + if (it.type.isNullable) " = null" else "" }
        val argsList = meth.args.joinToString(", ", "mapOf(", ")") { "\"${it.name}\" to Variable.make(\"${gqlType(it.type)}\", ${it.name})" }
        val argsTypes = meth.args.joinToString(", ", "mapOf(", ")") { "\"${it.name}\" to \"${gqlType(it.type)}\"" }

        if (meth.returnType.isScalar) {
            return """
fun ${meth.name}(${argsMapped}): Unit {
    builder.attr("${meth.name}", $argsTypes)
    builder.variables.putAll($argsList)
}
            """.trim()
        } else {
            return """
fun ${meth.name}(${argsMapped}, block: ${typeName(meth.returnType)}DSL.() -> Unit) {
    builder.scope("${meth.name}", $argsTypes) {
        ${typeName(meth.returnType)}DSL(this).block()
    }
    builder.variables.putAll($argsList)
}
            """.trim()
        }
    }

    fun remapType(type: Type): String {
        return if (type is ListType) {
            "List<${remapType(type.elementType)}>"
        } else {
            type.name
        } + if (type.isNullable) "?" else ""
    }

    fun getElement(key: String, type: Type): String {
        val c = if (type.isNullable) {
            "?."
        } else {
            "!!."
        }

        val (prefix, on, suffix) = if (type is ListType) {
            val inner = if (type.elementType.isNullable) {
                "it."
            } else {
                "it?."
            }

            Triple("json[\"$key\"]${c}jsonArray${c}map { ", inner, " }")

        } else {
            if (type.isNullable) {
                Triple("", "json[\"$key\"]${c}",  "")
            } else {
                Triple("", "json[\"$key\"]${c}", "")
            }
        }

        return when (type.name) {
            "ID", "String" -> "${prefix}${on}jsonPrimitive${c}content${suffix}"
            "Int" -> "${prefix}${on}jsonPrimitive${c}int${suffix}"
            "Float" -> "${prefix}${on}jsonPrimitive${c}float${suffix}"
            "Boolean" -> "${prefix}${on}jsonPrimitive${c}boolean${suffix}"
            else -> "${prefix}${on}jsonObject${c}let(::${typeName(type)})${suffix}"
        }
    }

    fun typeName(type: Type): String {
        return if (type is ListType) {
            return typeName(type.elementType)
        } else {
            type.name
        }
    }

    fun gqlType(type: Type): String {
        return if (type is ListType) {
            "[${gqlType(type.elementType)}]"
        } else {
            type.name
        } + if (!type.isNullable) "!" else ""
    }

    fun argType(type: Type): String {
        return when {
            type.isModel() -> {
                "${type.name}Model"
            }
            type is ListType -> {
                "List<${argType(type.elementType)}>"
            }
            else -> {
                type.name
            }
        } + if (type.isNullable) "?" else ""
    }

    fun constructor(type: Type) : String {
        if (type.isNullable) {
            val baseType = if (type is ListType) {
                ListType(type.elementType, false)
            } else {
                Type(type.name, type.isScalar, isNullable=false, isList=false)
            }
            return "{ it?.let(${constructor(baseType)}) }"
        }

        if (type is ListType) {
            return "{ it!!.jsonArray.map(${constructor(type.elementType)}) }"
        }

        if (type.isModel()) {
            return "{ ${type.name}(it!!.jsonObject) }"
        } else {
            return when(type.name) {
                "ID", "String" -> "{ it!!.jsonPrimitive.content }"
                "Int" -> "{ it!!.jsonPrimitive.int }"
                "Float" -> "{ it!!.jsonPrimitive.float }"
                "Boolean" -> "{ it!!.jsonPrimitive.boolean }"
                else -> throw IllegalArgumentException("Unknown type ${type.name}")
            }
        }
    }
}
