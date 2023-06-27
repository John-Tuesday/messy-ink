package org.calamarfederal.messyink.common.math

/**
 * Minimum and Maximum values
 *
 * primary constructor performs no checks. use [minMaxOf] instead to init [min] and [max]
 */
class MinMax<T : Comparable<T>>(
    /**
     * Minimum value
     *
     * Convention should follow:
     * `[min] <= [max]`
     */
    val min: T,
    /**
     * Maximum value
     *
     * Convention should follow:
     * `[min] <= [max]`
     */
    val max: T,
) : ClosedRange<T> {
    override val start = min
    override val endInclusive = max

    override fun equals(other: Any?): Boolean =
        if (other !is MinMax<*>) false else other.min == min && other.max == max

    override fun hashCode(): Int {
        var result = min.hashCode()
        result = 31 * result + max.hashCode()
        return result
    }

}

/**
 * Properly create and initialize [MinMax]
 */
fun <T : Comparable<T>> minMaxOf(a: T, b: T): MinMax<T> =
    MinMax(min = minOf(a, b), max = maxOf(a, b))

/**
 * [MinMax] which expands to include [other]
 */
infix fun <T : Comparable<T>> MinMax<T>.include(other: T): MinMax<T> =
    MinMax(min = minOf(min, other), max = maxOf(max, other))

/**
 * Gets the minimum and maximum in one sweep; Or null if empty
 *
 * ties will yield the first occurrence
 */
fun <T, R : Comparable<R>> Iterable<T>.minAndMaxOfOrNull(selector: (T) -> R): MinMax<R>? =
    fold<T, MinMax<R>?>(null) { acc, item ->
        val value = selector(item)
        acc?.include(value) ?: MinMax(value, value)
    }

/**
 * Gets the minimum and maximum in one sweep
 *
 * ties will yield the first occurrence
 */
fun <T, R : Comparable<R>> Iterable<T>.minAndMaxOf(selector: (T) -> R): MinMax<R> =
    minAndMaxOfOrNull(selector)!!
