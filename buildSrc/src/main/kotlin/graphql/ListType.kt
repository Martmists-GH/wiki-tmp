package graphql

class ListType(
    val elementType: Type,
    isNullable: Boolean,
) : Type(elementType.name, false, isNullable, true)
