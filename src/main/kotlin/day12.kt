package adventofcode

public fun main() {
    part1()
    part2()
}

private fun part1() {
    println(heightmap.path(start, end)!!.count())
}

private fun part2() {
    val lowestPoints = mutableListOf<Point>()
    for (y in heightmap.indices) {
        for (x in heightmap[y].indices) {
            if (heightmap.heightAt(x, y) == 0) lowestPoints += Point(x, y)
        }
    }

    lowestPoints
        .mapNotNull { point -> heightmap.path(point, end) }
        .minBy(Path::count)
        .count()
        .also(::println)
}

private lateinit var start: Point
private lateinit var end: Point

// Y increases downward.
private val heightmap = input
    .lineSequence()
    .onEachIndexed { row, columns ->
        val startIndex = columns.indexOf('S')
        if (startIndex != -1) start = Point(startIndex, row)

        val endIndex = columns.indexOf('E')
        if (endIndex != -1) end = Point(endIndex, row)
    }
    .map(String::toCharArray)
    .map { row ->
        row.map { col ->
            when (col) {
                'S' -> -1
                'E' -> 'z' - 'a'
                else -> col - 'a'
            }
        }.toTypedArray()
    }.toList().toTypedArray()

private typealias Heightmap = Array<Array<Int>>
private typealias Path = List<Point>
private fun Heightmap.heightAt(point: Point) = heightAt(point.x, point.y)
private fun Heightmap.heightAt(x: Int, y: Int) = this[y][x]
private fun Point.neighbors(): List<Point> = listOf(
    Point(x - 1, y), // West
    Point(x, y - 1), // North
    Point(x + 1, y), // East
    Point(x, y + 1), // South
)
private fun Heightmap.isInBounds(x: Int, y: Int) =
    x >= 0 && y >= 0 && y < size && x < get(y).size
private fun Heightmap.isInBounds(point: Point) = isInBounds(point.x, point.y)

private data class Node(val previous: Node? = null, val point: Point)

/** @return `null` if no path can be found. */
private fun Heightmap.path(start: Point, end: Point): Path? {
    require(isInBounds(start)) { "Start is out-of-bounds: $start" }
    require(isInBounds(end)) { "Start is out-of-bounds: $start" }

    val walked = mutableSetOf<Point>()
    val search = mutableListOf<Node>()
    search.add(Node(point = start))

    fun Node.path(): Path {
        val path = mutableListOf<Point>()
        var node: Node = this
        while (node.previous != null) {
            path += node.point
            node = node.previous!!
        }
        return path.reversed()
    }

    while (search.isNotEmpty()) {
        val node = search.removeFirst()
        if (node.point == end) return node.path()
        search.addAll(
            node.point
                .neighbors()
                .asSequence()
                .filterNot { it in walked } // Don't explore any points we've already explored.
                .filter(::isInBounds) // Don't go out-of-bounds.
                .filter { point -> heightAt(point) - heightAt(node.point) <= 1 } // Limit climb rate.
                .onEach { walked.add(it) } // Leave breadcrumbs of where we've explored.
                .map { Node(node, it) }
                .toList()
        )
    }
    return null
}

private fun Heightmap.render(path: Path) = buildString {
    for (y in this@render.indices) {
        for (x in this@render[y].indices) {
            append(if (Point(x, y) in path) 'X' else '.')
        }
        appendLine()
    }
}
