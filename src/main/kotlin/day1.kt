package adventofcode

public fun main() {
    part1()
    part2()
}

private fun part1() {
    caloriesPerElf
        .max()
        .also(::println)
}

private fun part2() {
    caloriesPerElf
        .sorted()
        .toList()
        .takeLast(3)
        .sum()
        .also(::println)
}

private val caloriesPerElf = sequence {
    var total = 0
    input
        .lineSequence()
        .forEach {
            if (it.isEmpty()) {
                yield(total)
                total = 0
            } else {
                total += it.toInt()
            }
        }
}
