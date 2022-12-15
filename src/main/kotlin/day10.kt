package adventofcode

import kotlin.math.abs

public fun main() {
    part1()
    part2()
}

private fun part1() {
    var x = 1
    var cycle = 1
    fun isSignalStrength(cycle: Int) = (cycle - 20) % 40 == 0
    sequence {
        input
            .lineSequence()
            .forEach { line ->
                if (isSignalStrength(++cycle)) yield(x * cycle)
                val parts = line.split(' ')
                if (parts[0] == "addx") {
                    cycle++
                    x += parts[1].toInt()
                    if (isSignalStrength(cycle)) yield(x * cycle)
                }
            }
    }.sum().also(::println)
}

private fun part2() {
    sequence {
        var x = 1
        input.lineSequence().forEach { line ->
            val parts = line.split(' ')
            yield(x)
            if (parts[0] == "addx") {
                yield(x)
                x += parts[1].toInt()
            }
        }
    }.toList()
        .mapIndexed { index, x -> abs(index % 40 - x) < 2 }
        .map { if (it) '#' else '.' }
        .joinToString(separator = "")
        .chunked(40)
        .joinToString(separator = "\n")
        .also(::println)
}
