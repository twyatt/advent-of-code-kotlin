package adventofcode

public fun main() {
    part1()
    part2()
}

private fun part1() {
    assignments
        .count { (a, b) -> a in b || b in a }
        .also(::println)
}

private fun part2() {
    assignments
        .count { (a, b) -> a.intersect(b).isNotEmpty() }
        .also(::println)
}

private val assignments = input
    .lineSequence()
    .map { line ->
        val (a, b, c, d) = line.split('-', ',').map(String::toInt)
        a..b to c..d
    }

private operator fun IntRange.contains(other: IntRange) =
    first >= other.first && last <= other.last
