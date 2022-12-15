package adventofcode

import adventofcode.Mode.Crates
import adventofcode.Mode.Moves

public fun main() {
    part1()
}

private fun part1() {
    val crates = mutableListOf<List<Char>>()
    val moves = mutableListOf<Move>()

    for (line in input.lines()) {
        if (line.isEmpty()) continue
        when {
            CrateLabelsRegex.matches(line) -> mode = Moves
            mode == Crates -> crates += line.drop(1).windowed(size = 1, step = 4).map(String::first)
            mode == Moves -> moves += line.toMove()
        }
    }

    val stacks = mutableMapOf<String, MutableList<Char>>()
    crates.reversed().forEach { row ->
        row.forEachIndexed { index, crate ->
            if (crate != ' ') {
                stacks
                    .getOrPut("${index + 1}", ::mutableListOf)
                    .add(crate)
            }
        }
    }

    moves.forEach { move ->
        repeat(move.quantity) {
            val crate = stacks[move.from]!!.removeLast()
            stacks[move.to]!!.add(crate)
        }
    }

    stacks.values.map { it.last() }.joinToString(separator = "").also(::println)
}

private val CrateLabelsRegex = """(?: \d  ?)+""".toRegex()
private val MoveRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()

private enum class Mode { Crates, Moves }
private var mode = Crates

private data class Move(val quantity: Int, val from: String, val to: String)
private fun CharSequence.toMove(): Move {
    val (quantity, from, to) = MoveRegex.matchEntire(this)!!.destructured
    return Move(quantity.toInt(), from, to)
}
