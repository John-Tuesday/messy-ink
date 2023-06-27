package org.calamarfederal.messyink.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Elevation Levels as determined by Material Design 3 spec
 */
val MaterialTheme.materialLevels: List<MaterialLevel>
    get() = MaterialLevel.allLevels

/**
 * Round down to nearest [MaterialLevel]; coerce into bounds
 */
fun Dp.toMaterialLevelFloor(): MaterialLevel = MaterialLevel.floor(this)

/**
 * Round up to nearest [MaterialLevel]; coerce into bounds
 */
fun Dp.toMaterialLevelCiel(): MaterialLevel = MaterialLevel.ciel(this)

/**
 * Elevation level to Z-height in dp
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
        internal val allLevels = listOf(0, 1, 3, 6, 8, 12).mapIndexed { index, height ->
            MaterialLevel(
                level = index,
                elevation = height.dp
            )
        }

        /**
         * Get [MaterialLevel] with [level] clamped inclusively between `0` and size of [allLevels]
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
