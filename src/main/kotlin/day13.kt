package adventofcode

import adventofcode.Element.Array
import adventofcode.Element.Number

public fun main() {
    part1()
    part2()
}

private fun part1() {
    sequence {
        var index = 0
        with(input.lineSequence().iterator()) {
            while (hasNext()) {
                index++
                val left = next().elementIterator().parse() as Item.Array
                val right = next().elementIterator().parse() as Item.Array
                if (inOrder(left, right) != false) yield(index)
                if (hasNext()) next()
            }
        }
    }.sum().also(::println)
}

private fun part2() {
    val comparator = Comparator<Item> { left, right ->
        when (inOrder(left, right)) {
            true -> -1
            false -> 1
            null -> 0
        }
    }
    val packets = "$input\n\n[[2]]\n[[6]]"
        .lineSequence()
        .filterNot(String::isEmpty)
        .map(String::elementIterator)
        .map(ElementIterator::parse)
        .toList()
        .sortedWith(comparator)
        .map(Item::toString)
    println((packets.indexOf("[[2]]") + 1) * (packets.indexOf("[[6]]") + 1))
}

private fun inOrder(left: Item, right: Item): Boolean? {
    return when {
        left is Item.Number && right is Item.Number -> when {
            left.value < right.value -> true
            left.value > right.value -> false
            else -> null
        }
        left is Item.Array && right is Item.Array -> {
            val leftIterator = left.items.iterator()
            val rightIterator = right.items.iterator()
            while (leftIterator.hasNext() && rightIterator.hasNext()) {
                val result = inOrder(leftIterator.next(), rightIterator.next())
                if (result != null) return result
            }
            (!leftIterator.hasNext()).takeIf { leftIterator.hasNext() != rightIterator.hasNext() }
        }
        left is Item.Number && right is Item.Array -> inOrder(itemArrayOf(left), right)
        left is Item.Array && right is Item.Number -> inOrder(left, itemArrayOf(right))
        else -> error("!!")
    }
}

private sealed class Item {
    data class Array(val items: List<Item>) : Item() {
        override fun toString(): String = "[${items.joinToString(", ", transform = Item::toString)}]"
    }
    data class Number(val value: Int) : Item() { override fun toString(): String = value.toString() }
}
private fun itemArrayOf(item: Item) = Item.Array(listOf(item))

private fun ElementIterator.parse(): Item = when (val element = next()) {
    is Array.Start -> parseArray()
    is Array.End -> error("Unexpected end of array")
    is Number -> Item.Number(element.value)
}

private fun ElementIterator.parseArray(): Item.Array {
    val items = mutableListOf<Item>()
    do {
        val element = next()
        items += when (element) {
            is Array.Start -> parseArray()
            is Array.End -> break
            is Number -> Item.Number(element.value)
        }
    } while (element != Array.End)
    return Item.Array(items)
}

private sealed class Element {
    sealed class Array : Element() {
        object Start : Array() { override fun toString(): String = "Start" }
        object End : Array() { override fun toString(): String = "End" }
    }
    data class Number(val value: Int) : Element() { override fun toString(): String = value.toString() }
}

private fun CharSequence.elementIterator() = ElementIterator(this)
private class ElementIterator(private val input: CharSequence) : Iterator<Element> {
    private var position = 0
    override fun hasNext(): Boolean = position < input.length
    override fun next(): Element {
        while (input[position] == ',') {
            position++
            if (!hasNext()) error("EOF: $input")
        }
        when (val c = input[position++]) {
            '[' -> return Array.Start
            ']' -> return Array.End
            else -> {
                if (c.isDigit()) {
                    val digitAccumulator = mutableListOf(c)
                    while (input[position].isDigit()) {
                        digitAccumulator += input[position]
                        position++
                    }
                    val number = digitAccumulator.joinToString(separator = "")
                    return Number(number.toInt())
                } else {
                    error("Unexpected char at $position: $c")
                }
            }
        }
    }
}
