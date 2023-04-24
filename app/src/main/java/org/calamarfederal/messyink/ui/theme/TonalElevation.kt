package org.calamarfederal.messyink.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

object TonalElevation {
    private val heights = arrayListOf(0.dp, 1.dp, 3.dp, 6.dp, 8.dp, 12.dp)



    fun heightToLayer(height: Dp): Int {
        for ((index, floor) in heights.withIndex().reversed()) {
            if (height >= floor)
                return index
        }
        return 0
    }

    fun heightOfNext(height: Dp, minimumHeight: Dp = Dp.Unspecified, minimumLayer: Int = 0): Dp {
        require(minimumLayer < heights.size && minimumLayer > 0)

        val testHeight = if (minimumHeight.isSpecified) height.coerceAtLeast(minimumHeight) else height

        for (floor in heights.subList(minimumLayer, heights.size)) {
            if (testHeight < floor)
                return floor
        }
        return if (minimumHeight.isSpecified) minimumHeight else height
    }

    fun heightOfPrevious(height: Dp, maximumHeight: Dp = Dp.Unspecified, maximumLayer: Int = 0): Dp {
        require(maximumLayer < heights.size)

        for (floor in heights.subList(0, maximumLayer + 1).reversed()) {
            if (height > floor && floor <= maximumHeight)
                return floor
        }
        return if (maximumHeight.isSpecified) maximumHeight else heights.first()
    }
}

//val MaterialTheme.tonalLayers get() =
