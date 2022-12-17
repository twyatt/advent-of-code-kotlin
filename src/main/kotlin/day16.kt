package adventofcode

import adventofcode.Action.MoveTo
import adventofcode.Action.Open
import kotlin.math.max

public fun main() {
    part1()
}

private fun Sequence<Tick>.onEachMax(action: (Int) -> Unit): Sequence<Tick> {
    var max = Int.MIN_VALUE
    return onEach {
        max = max(max, it.pressureReleasedAt(30))
    }
}

private fun part1() {
    val start = valves.first { it.name == "AA" }
    val path = path(start, 30).maxBy { it.pressureReleasedAt(30) }
    (path.history + path).drop(1).forEach { tick ->
        println("== Minute ${tick.time} ==")
        val message = if (tick.opened.isEmpty()) {
            "No valves are open."
        } else {
            "Valve(s) ${tick.opened.joinToString(", ")} are open, releasing ${valves.pressure(tick)} pressure."
        }
        println(message)
        val action = when (tick.action) {
            Open -> "open"
            MoveTo -> "move to"
            null -> "do nothing"
        }
        println("You $action valve ${tick.target}.")
        println()
    }
    println(path.pressureReleasedAt(30))
}

private enum class Action { Open, MoveTo }
private data class Tick(
    val history: List<Tick>, // List of ticks (moments in time) to get to this tick.
    val visited: List<Valve>, // List of valves that have been visited at this tick (moment in time).
    val time: Int,
    val target: Valve,
    val action: Action?,
    val opened: Set<String>, // Set of opened valve names.
) {
    override fun toString(): String = "Tick@$time(target=$target, action=$action)"
}

private const val TIME_TO_OPEN_VALVE = 1
private fun Tick.pressureReleasedAt(time: Int): Int {
    var pressure = 0
    history.forEach { tick ->
        if (tick.action == Open) {
            pressure += tick.target.flowRate * (time - TIME_TO_OPEN_VALVE - tick.time).coerceAtLeast(0)
        }
    }
    return pressure
}

private fun Tick.canOpen() = target.flowRate > 0 && target.name !in opened
private fun Tick.open() = Tick(history + this, visited, time + 1, target, Open, opened + target.name)
private fun Tick.moveTo(valve: Valve) = Tick(
    history = history + this,
    visited = visited + target,
    time = time + 1,
    target = valve,
    action = MoveTo,
    opened = opened,
)
private fun Tick.doNothing() = Tick(history + this, visited, time + 1, target, null, opened)

private fun path(valve: Valve, maxTime: Int) = sequence {
    val openable = valves.filter { it.flowRate > 0 }.map(Valve::name).toSet().count()
    var found = 0
    val stack = mutableListOf<Tick>()
    stack.add(Tick(emptyList(), listOf(valve), 0, valve, null, emptySet()))
    while (stack.isNotEmpty()) {
        val tick = stack.removeFirst()
        if (tick.time == maxTime) {
            yield(tick)
            found++
        } else if (openable == tick.opened.count()) {
            stack.add(tick.doNothing())
        } else {
            if (tick.canOpen()) stack.add(tick.open())
            tick.target
                .neighbors
                .filterNot { it == tick.history.lastOrNull()?.target } // Don't immediately move back to where we just came from.
                .map(tick::moveTo)
                .also(stack::addAll)
        }
    }
    check(found > 0) { "No paths founds" }
}

private val InputRegex =
    """Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (.*)""".toRegex()

private val valves = example
    .lineSequence()
    .map { line -> InputRegex.matchEntire(line)!!.destructured }
    .map { (name, flowRate, tunnels) ->
        Valve(name, flowRate.toInt(), tunnels.split(", "))
    }.toList()

private fun Iterable<Valve>.pressure(tick: Tick) =
    filter { it.name in tick.opened }
        .filterNot { it.name == tick.target.name && tick.action == Open }
        .sumOf { it.flowRate }

private data class Valve(
    val name: String,
    val flowRate: Int,
    val leadsTo: List<String>,
) {
    val neighbors by lazy {
        leadsTo.map { search -> valves.first { it.name == search } }
    }

    override fun toString(): String = name
}
