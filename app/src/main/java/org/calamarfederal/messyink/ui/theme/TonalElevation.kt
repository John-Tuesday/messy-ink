package org.calamarfederal.messyink.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min

/**
 * Model the (tonal) Material Layers from M3 spec
 */
object TonalElevation {
    /**
     * [List] of each layer and their corresponding height
     */
    val layers = listOf(0.dp, 1.dp, 3.dp, 6.dp, 8.dp, 12.dp)

    /**
     * @return Material Layer with height <= [height]
     */
    fun heightToLayer(height: Dp): Int {
        for ((index, floor) in layers.withIndex().reversed()) {
            if (height >= floor)
                return index
        }
        return 0
    }

    /**
     * @return the height in [Dp] of the Material Layer above [height]
     */
    fun heightOfNext(height: Dp, minimumHeight: Dp = Dp.Unspecified, minimumLayer: Int = 0): Dp {
        require(minimumLayer < layers.size && minimumLayer >= 0)

        val testHeight =
            if (minimumHeight.isSpecified) height.coerceAtLeast(minimumHeight) else height

        for (floor in layers.subList(minimumLayer, layers.size)) {
            if (testHeight < floor)
                return floor
        }
        return if (minimumHeight.isSpecified) minimumHeight else height
    }

    /**
     * @return height in [Dp] of the Material Layer below [height]
     */
    fun heightOfPrevious(
        height: Dp,
        maximumHeight: Dp = Dp.Unspecified,
        maximumLayer: Int = 0,
    ): Dp {
        require(maximumLayer < layers.size)

        for (floor in layers.subList(0, maximumLayer + 1).reversed()) {
            if (height > floor && floor <= maximumHeight)
                return floor
        }
        return if (maximumHeight.isSpecified) maximumHeight else layers.first()
    }
}

/**
 * Should supersede [TonalElevation]
 */
class MaterialLevel private constructor(
    /**
     * level according to Material Design 3
     */
    val level: Int,
    /**
     * elevation of [level] according to Material Design 3
     *
     * for use as TonalElevation or ShadowElevation
     */
    val elevation: Dp,
) : Comparable<MaterialLevel> {
    /**
     * next level; level is mod max in the resulting function body
     */
    operator fun inc(): MaterialLevel = MaterialLevel(level = level + 1)

    /**
     * previous level; level is mod max in the resulting function body
     */
    operator fun dec(): MaterialLevel = MaterialLevel(level = level - 1)

    /**
     * compare [level] of this and [other]
     */
    override fun compareTo(other: MaterialLevel) = level.compareTo(other.level)

    /**
     * compare [elevation] to [height]
     */
    operator fun compareTo(height: Dp): Int = elevation.compareTo(height)

    /**
     * compare [Dp] to [elevation]
     */
    operator fun Dp.compareTo(level: MaterialLevel): Int = compareTo(level.elevation)

    companion object {
        private val allLevels = listOf(0, 1, 3, 6, 8, 12).mapIndexed { index, height ->
            MaterialLevel(
                level = index,
                elevation = height.dp
            )
        }

        /**
         * Get [MaterialLevel] with [level] clamped inclusively between 0 and [allLevels.size]
         */
        operator fun invoke(level: Int = 0): MaterialLevel =
            allLevels[level.coerceIn(0, allLevels.size)]

        /**
         * round [height] up to the next [MaterialLevel]
         */
        fun ciel(height: Dp, level: Int = 0): MaterialLevel = allLevels
            .drop(level)
            .firstOrNull { it >= height }
            ?: allLevels.last()

        /**
         * round [height] down to previous [MaterialLevel]
         */
        fun floor(height: Dp, level: Int = allLevels.size): MaterialLevel = allLevels
            .takeLast(level - allLevels.size)
            .lastOrNull { it <= height }
            ?: allLevels.first()
    }
}
