package adventofcode

private val day: Int
    get() = Throwable()
        .stackTrace
        .asSequence()
        .map {
            Class.forName(it.className)
                .name
                .substringAfterLast('.')
                .substringBefore("$")
                .lowercase()
        }
        .first { it.startsWith("day") }
        .removePrefix("day")
        .removeSuffix("kt")
        .toInt()

public val example: String get() = readExample(day)
public val input: String get() = readInput(day)

private fun readExample(day: Int): String =
    readResource("/examples/day$day").removeSuffix("\n")

private fun readInput(day: Int): String =
    readResource("/inputs/day$day").removeSuffix("\n")

private fun readResource(path: String): String =
    checkNotNull(readResourceOrNull(path)) { "Resource not found: $path" }

private fun readResourceOrNull(path: String): String? =
    object {}::class.java.getResource(path)?.readText()
