package adventofcode

import kotlin.math.abs

public fun main() {
    part1()
    part2()
}

private fun part1() {
    val y = 2_000_000
    val deadspaceAtY = deadspaces.mapNotNull { region -> region.rangeAt(y) }

    val deadXs = mutableSetOf<Int>()
    deadspaceAtY.forEach { range -> deadXs.addAll(range.toSet()) }
    val remaining = deadXs - beacons.filter { it.y == y }.map(Point::x).toSet()
    println(remaining.count())
}

private fun part2() {
    val searchX = 0..4_000_000
    val searchY = 0..4_000_000
    val location = deadspaces
        .flatMap(Region::border)
        .filter { (x, y) -> x in searchX && y in searchY }
        .filter { point -> deadspaces.none { region -> point in region } }
        .first()
    println(location.x * 4_000_000L + location.y)
}

private val InputRegex =
    """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)"""
        .toRegex()

private val artifacts = input
    .lineSequence()
    .map { line -> InputRegex.matchEntire(line)!! }
    .map { match -> match.groupValues.drop(1).map(String::toInt) }
    .map { (sensorX, sensorY, beaconX, beaconY) -> Point(sensorX, sensorY) to Point(beaconX, beaconY) }
private val beacons = artifacts.map { (_, beacon) -> beacon }
private val deadspaces = artifacts.map { (sensor, beacon) ->
    Region(sensor, sensor.manhattanDistance(beacon))
}

private data class Region(val center: Point, val radius: Int)
private operator fun Region.contains(point: Point) = center.manhattanDistance(point) <= radius

private fun Region.rangeAt(y: Int): IntRange? {
    val dy = abs(y - center.y)
    if (dy > radius) return null
    val dx = radius - dy
    return (center.x - dx)..(center.x + dx)
}

/** @return [Sequence] of points that border this [Region]. */
private fun Region.border() = sequence {
    var ty = center.y
    var by = center.y
    ((center.x - radius - 1)..center.x).forEach { x ->
        yield(Point(x, ty++))
        yield(Point(x, by--))
    }
    ty = center.y
    by = center.y
    ((center.x + radius + 1) downTo center.x).forEach { x ->
        yield(Point(x, ty++))
        yield(Point(x, by--))
    }
}.toSet()

private fun Point.manhattanDistance(other: Point) = abs(x - other.x) + abs(y - other.y)
