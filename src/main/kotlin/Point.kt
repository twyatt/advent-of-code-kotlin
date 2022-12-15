package adventofcode

public data class Point(val x: Int = 0, val y: Int = 0) {
    override fun toString(): String = "[$x, $y]"
}
public operator fun Point.minus(other: Point): Point = Point(x - other.x, y - other.y)
