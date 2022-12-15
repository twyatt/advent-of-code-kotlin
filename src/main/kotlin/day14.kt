package adventofcode

import java.lang.Integer.max
import java.lang.Integer.min

public fun main() {
    part1()
    part2()
}

private fun part1() {
    val world = World(source, rock)
    var sand = 0
    while (world.placeSand()) sand++
    println(sand)
}

private fun part2() {
    val floorY = rock.maxBy(Point::y).y + 2
    val world = World(source, rock, floorY = floorY)
    var sand = 0
    while (world.placeSand()) sand++
    println(sand)
}

private val source = Point(500, 0)
private val rock = input
    .lineSequence()
    .map { line ->
        line.split(" -> ").map {
            it.split(',', limit = 2).map(String::toInt).let { (x, y) -> Point(x, y) }
        }
    }.flatMap { rock ->
        rock
            .windowed(size = 2, step = 1)
            .flatMap { (from, to) ->
                when {
                    from.x == to.x -> (from.y rangeTo to.y).map { Point(from.x, it) }
                    from.y == to.y -> (from.x rangeTo to.x).map { Point(it, from.y) }
                    else -> error("!!")
                }
            }
    }.toSet()

// Y increases downward.
private data class World(
    val source: Point,
    val rock: Set<Point>,
    val sand: MutableSet<Point> = mutableSetOf(),
    val floorY: Int? = null,
) {
    override fun toString(): String = buildString {
        val (min, max) = range(setOf(source), rock, sand)
        for (y in min.y..max.y) {
            for (x in min.x..max.x) {
                val symbol = when (Point(x, y)) {
                    source -> '+'
                    in rock -> '#'
                    in sand -> 'o'
                    else -> '.' // Air
                }
                append(symbol)
            }
            appendLine()
        }
    }
}

private fun World.placeSand(): Boolean {
    if (isOccupied(source)) return false // Sand has filled to the brim!
    val maxY = rock.maxBy(Point::y).y
    var position = source
    while (true) {
        position = position.movedBy(dy = 1).takeIf(::isNotOccupied)
            ?: position.movedBy(dx = -1, dy = 1).takeIf(::isNotOccupied)
            ?: position.movedBy(dx = 1, dy = 1).takeIf(::isNotOccupied)
            ?: break

        if (floorY != null) {
            if (position.y + 1 == floorY) break // Landed on the floor.
        } else {
            if (position.y > maxY) return false // Fell into the abyss below.
        }
    }
    sand += position
    return true
}

private fun World.isOccupied(point: Point) = point in rock || point in sand
private fun World.isNotOccupied(point: Point) = !isOccupied(point)

private infix fun Int.rangeTo(other: Int): IntRange =
    if (this > other) other..this else this..other

/** @return [Pair] of min and max [Point]s within [points] sets. */
private fun range(vararg points: Set<Point>): Pair<Point, Point> {
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var minY = Int.MAX_VALUE
    var maxY = Int.MIN_VALUE
    points.forEach {
        it.forEach { point ->
            minX = min(point.x, minX)
            maxX = max(point.x, maxX)
            minY = min(point.y, minY)
            maxY = max(point.y, maxY)
        }
    }
    return Point(minX, minY) to Point(maxX, maxY)
}

private fun Point.movedBy(dx: Int = 0, dy: Int = 0) =
    copy(x = x + dx, y = y + dy)
