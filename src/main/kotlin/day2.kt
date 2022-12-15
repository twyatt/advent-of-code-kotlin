package adventofcode

public fun main() {
    part1()
    part2()
}

private fun part1() {
    input
        .lineSequence()
        .map { line ->
            val (them, _, you) = line.toCharArray()
            score(you) + outcome(you, them)
        }.sum().also(::println)
}

private fun part2() {
    fun move(them: Char, outcome: Char): Char =
        listOf('X', 'Y', 'Z').first { you -> outcome == 'X' + outcome(you, them) / 3 }
    input
        .lineSequence()
        .map { line ->
            val (them, _, outcome) = line.toCharArray()
            val you = move(them, outcome)
            score(you) + outcome(you, them)
        }.sum().also(::println)
}

private fun score(move: Char): Int = move - 'X' + 1

private fun outcome(you: Char, them: Char): Int = when {
    you == 'X' /* Rock     */ && them == 'A' /* Rock     */ -> 3 // Draw
    you == 'Y' /* Paper    */ && them == 'B' /* Paper    */ -> 3 // Draw
    you == 'Z' /* Scissors */ && them == 'C' /* Scissors */ -> 3 // Draw

    you == 'X' /* Rock     */ && them == 'C' /* Scissors */ -> 6 // Win
    you == 'Y' /* Paper    */ && them == 'A' /* Rock     */ -> 6 // Win
    you == 'Z' /* Scissors */ && them == 'B' /* Paper    */ -> 6 // Win

    else -> 0 // Loss
}
