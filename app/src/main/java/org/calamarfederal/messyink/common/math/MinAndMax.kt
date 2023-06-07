package org.calamarfederal.messyink.common.math

/**
 * holds the lowest and highest value, but [min] < [max] is not enforced
 *
 * @property[min] minimum
 * @property[max] maximum
 */
data class MinMax<T : Comparable<T>>(
    val min: T,
    val max: T,
) : ClosedRange<T> {
    override val start = min
    override val endInclusive = max
}

/**
 * Gets the minimum and maximum in one sweep; Or null if empty
 *
 * ties will yield the first occurrence
 */
fun <T, R : Comparable<R>> Iterable<T>.minAndMaxOfOrNull(selector: (T) -> R): MinMax<R>? =
    fold<T, MinMax<R>?>(null) { acc, item ->
        val value = selector(item)
        acc?.copy(
            min = minOf(acc.min, value),
            max = maxOf(acc.max, value),
        ) ?: MinMax(value, value)
    }

/**
 * Gets the minimum and maximum in one sweep
 *
 * ties will yield the first occurrence
 */
fun <T, R : Comparable<R>> Iterable<T>.minAndMaxOf(selector: (T) -> R): MinMax<R> =
    minAndMaxOfOrNull(selector)!!