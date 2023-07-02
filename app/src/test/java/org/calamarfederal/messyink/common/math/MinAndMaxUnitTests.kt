package org.calamarfederal.messyink.common.math

import org.junit.Assert.*

import org.junit.Test

class MinAndMaxUnitTests {

    @Test
    fun `include ignores when other is already in bounds`() {
        val minMax = MinMax(min = 7, max = 10)
        val other = 9
        val expect = MinMax(min = 7, max = 10)

        assert(minMax include other == expect)
    }

    @Test
    fun `include expands in the correct direction`() {
        val minMax = MinMax(min = 7, max = 10)
        val otherLower = 5
        val otherHigher = 12
        val expectLower = MinMax(min = 5, max = 10)
        val expectHigher = MinMax(min = 7, max = 12)

        assert(minMax include otherLower == expectLower)
        assert(minMax include otherHigher == expectHigher)
    }

    @Test
    fun `include keeps equality when other is equal to boundary`() {
        val minMax = MinMax(min = 7, max = 10)
        val otherMin = 7
        val otherMax = 10
        val expect = MinMax(min = 7, max = 10)

        assert(minMax include otherMin == expect)
        assert(minMax include otherMax == expect)
    }

    @Test
    fun `minAndMaxOf Ordered data`() {
        val data = (-10 .. 10)
        val expect = MinMax(min = -10, max = 10)

        assert(data.minAndMaxOf { it } == expect)
    }

    @Test
    fun `minAndMaxOf unordered data`() {
        val data = (-10 .. 10).shuffled()
        val expect = MinMax(min = -10, max = 10)

        assert(data.minAndMaxOf { it } == expect)
    }

    @Test
    fun `minAndMaxOf equal data`() {
        val data = listOf(10, 10, 10, 10, 10, 10, 10, 10)
        val expect = MinMax(min = 10, max = 10)

        assert(data.minAndMaxOf { it } == expect)
    }

    data class CmpId(
        val value: Int,
        val id: String,
    ) : Comparable<CmpId> {
        override fun compareTo(other: CmpId): Int = value.compareTo(other.value)
    }

    @Test
    fun `minAndMaxOf preserves initial order`() {
        val data = (1 .. 100).shuffled().flatMap {
            listOf(
                CmpId(value = it, id = "first $it"),
                CmpId(value = it, id = "second $it"),
            )
        }
        val expect = MinMax(
            min = CmpId(value = 1, id = "first 1"),
            max = CmpId(value = 100, id = "first 100"),
        )

        assert(data.minAndMaxOf { it } == expect)
    }
}
