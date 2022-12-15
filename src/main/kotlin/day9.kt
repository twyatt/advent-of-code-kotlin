package adventofcode

public fun main() {
    part1()
    part2()
}

private fun part1() {
    val head = Knot()
    val tail = Knot()

    input
        .lineSequence()
        .map { line -> line.split(' ') }
        .map { (direction, count) -> direction.first() to count.toInt() }
        .flatMap { (direction, count) ->
            sequence {
                repeat(count) {
                    yield(direction)
                }
            }
        }
        .runningFold(head, Knot::move).last().path
        .runningFold(tail, Knot::follow).last().path
        .toSet().count()
        .also(::println)
}

private fun part2() {
    val head = Knot()
    val tails = List(10) { Knot() }

    input
        .lineSequence()
        .map { line -> line.split(' ') }
        .map { (direction, count) -> direction.first() to count.toInt() }
        .flatMap { (direction, count) ->
            sequence {
                repeat(count) {
                    yield(direction)
                }
            }
        }
        .runningFold(head, Knot::move).last().path
        .runningFold(tails[0], Knot::follow).last().path
        .runningFold(tails[1], Knot::follow).last().path
        .runningFold(tails[2], Knot::follow).last().path
        .runningFold(tails[3], Knot::follow).last().path
        .runningFold(tails[4], Knot::follow).last().path
        .runningFold(tails[5], Knot::follow).last().path
        .runningFold(tails[6], Knot::follow).last().path
        .runningFold(tails[7], Knot::follow).last().path
        .runningFold(tails[8], Knot::follow).last().path
        .toSet().count()
        .also(::println)
}

/** @return Distance squared. */
private fun Point.dist2(other: Point): Int {
    val dx = x - other.x
    val dy = y - other.y
    return dx * dx + dy * dy
}

private data class Knot(
    var position: Point = Point(),
    val path: MutableList<Point> = mutableListOf(Point(0, 0)),
)

private fun Knot.move(direction: Char): Knot {
    position = when (direction) {
        'U' -> position.copy(y = position.y + 1)
        'D' -> position.copy(y = position.y - 1)
        'L' -> position.copy(x = position.x - 1)
        'R' -> position.copy(x = position.x + 1)
        else -> error("Unknown direction: $direction")
    }
    path += position
    return this
}

/** Max distance squared. */
private val MaxDistance2 = Point().dist2(Point(1, 1))

private fun Knot.follow(point: Point): Knot {
    if (position.dist2(point) <= MaxDistance2) return this // No need to move if already close enough.
    val diff = point - this.position
    position = position.copy(
        x = position.x + diff.x.coerceIn(-1, 1),
        y = position.y + diff.y.coerceIn(-1, 1),
    )
    path += position
    return this
}
