package graphql

class MethodDef(
    val name: String,
    val returnType: Type,
    val args: List<PropertyDef>,
)
