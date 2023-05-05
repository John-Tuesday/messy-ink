package org.calamarfederal.messyink.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

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

//val MaterialTheme.tonalLayers get() =
