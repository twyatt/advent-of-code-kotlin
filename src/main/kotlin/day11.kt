package adventofcode

import adventofcode.ReductionMode.DivideBy3
import adventofcode.ReductionMode.Subtraction

public fun main() {
    println(monkeyBusiness(rounds = 20, mode = DivideBy3)) // Part 1
    println(monkeyBusiness(rounds = 10_000, mode = Subtraction)) // Part 2
}

private enum class ReductionMode { DivideBy3, Subtraction }

private fun monkeyBusiness(rounds: Int, mode: ReductionMode): Long {
    val monkeys = mutableListOf<Monkey>()
    val lines = input.lineSequence().iterator()
    while (lines.hasNext()) {
        with(lines) {
            monkeys += Monkey(
                number = next().substringAfter(' ').removeSuffix(":").toInt(),
                items = next().substringAfter("Starting items: ").split(", ").map(String::toLong).toMutableList(),
                operation = next().substringAfter("Operation: new = "),
                test = next().substringAfter("Test: divisible by ").toLong(),
                ifTrue = next().substringAfterLast(' ').toInt(),
                ifFalse = next().substringAfterLast(' ').toInt(),
            )
            if (hasNext()) next()
        }
    }

    val reducer = monkeys.map { it.test }.reduce { accumulator, next -> accumulator * next } // Only used for part 2.

    repeat(rounds) {
        monkeys.forEach { monkey ->
            val (_, operand, right) = monkey.operation.split(' ')
            val iterator = monkey.items.iterator()
            iterator.forEach { item ->
                monkey.inspections++
                var worry = when (operand) {
                    "*" -> if (right == "old") item * item else item * right.toLong()
                    "+" -> item + right.toLong()
                    else -> error("Unsupported operation: ${monkey.operation}")
                }
                when (mode) {
                    DivideBy3 -> worry /= 3L
                    Subtraction -> while (worry > reducer) worry -= reducer
                }
                val test = worry % monkey.test == 0L
                val target = if (test) monkey.ifTrue else monkey.ifFalse
                monkeys[target].items += worry
                iterator.remove()
            }
        }
    }
    val top2 = monkeys.sortedBy { it.inspections }.takeLast(2)
    return top2[0].inspections * top2[1].inspections
}

private data class Monkey(
    val number: Int,
    val items: MutableList<Long>,
    val operation: String,
    val test: Long,
    val ifTrue: Int,
    val ifFalse: Int,
    var inspections: Long = 0,
)
