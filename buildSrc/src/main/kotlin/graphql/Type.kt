package graphql

open class Type(
    val name: String,
    val isScalar: Boolean,
    val isNullable: Boolean,
    val isList: Boolean,
) {
    fun isModel() = !isScalar && !isList

    companion object {
        val scalars = setOf(
            "String",
            "Int",
            "Float",
            "Boolean",
            "ID",
        )
    }
}
