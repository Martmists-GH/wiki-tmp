package team.exr.ext

fun String.toHumanString() : String = split('_').joinToString(" ") {
    it.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}

private val URL_INVALID_REGEX = Regex("[^a-z0-9_\\-/]+")

fun String.toUrlString() : String = split(' ').joinToString("-") { URL_INVALID_REGEX.replace(it.lowercase(), "") }
