package org.calamarfederal.messyink.common.compose.charts

import androidx.compose.ui.geometry.Offset

/**
 * A 2D point of any kind
 *
 *      y
 *       |
 *       |
 *       |
 *       |________
 *                x
 *
 * @property[x] - horizontal axis
 * @property[y] - vertical axis
 */
private interface Point2d<T : Number> {
    val x: T
    val y: T
}

/**
 * A 3D point of any kind
 *
 *       z
 *        |
 *        |
 *        |
 *        |________
 *       /          y
 *      /
 *     /
 *    x
 *
 * @property[x] - depth
 * @property[y] - horizontal
 * @property[z] - height
 */
private interface Point3d<T : Number> {
    val x: T
    val y: T
    val z: T
}

/**
 * Point in chart defined using it's percentage of the total size
 */
data class PointByPercent(
    /**
     * Percentage of total width
     */
    val x: Double,
    /**
     * Percentage of total height
     */
    val y: Double,
) {
    constructor(pair: Pair<Number, Number>) : this(pair.first.toDouble(), pair.second.toDouble())

    /**
     * Scale each axis independently
     *
     * `([x] * [scaleX], [y] * [scaleY])`
     */
    fun toScale(scaleX: Number = 1, scaleY: Number = 1) =
        PointByPercent(x * scaleX.toDouble(), y * scaleY.toDouble())

    /**
     * Scalar multiply
     *
     * `(x * n, y * n)`
     */
    operator fun times(n: Number) = PointByPercent(x = x * n.toDouble(), y = y * n.toDouble())

    /**
     * Scalar divide
     *
     * `(x / n, y / n)`
     */
    operator fun div(n: Number) = PointByPercent(x = x / n.toDouble(), y = y / n.toDouble())
}

/**
 * Convert [x][PointByPercent.x] and [y][PointByPercent.y] to their nearest [Float]
 */
fun PointByPercent.toFloat() = FloatPointByPercent(x = x.toFloat(), y = y.toFloat())

/**
 * Convert [x][PointByPercent.x] and [y][PointByPercent.y] to their nearest [Float]
 */
fun PointByPercent.toOffset() = Offset(x = x.toFloat(), y = y.toFloat())

/**
 * Convert [x][FloatPointByPercent.x] and [y][FloatPointByPercent.y] to [Double]
 */
fun FloatPointByPercent.toPointByPercent() = PointByPercent(x = x.toDouble(), y = y.toDouble())

/**
 * Returns an [Offset] with the same `x` and `y`
 */
fun FloatPointByPercent.toOffset() = Offset(x = x, y = y)

/**
 * Point defined by percentage of total size
 *
 * [Float] backed version of [PointByPercent]
 */
data class FloatPointByPercent(
    /**
     * Percentage of total width
     */
    val x: Float,
    /**
     * Percentage of total height
     */
    val y: Float,
) {
    constructor(pair: Pair<Number, Number>) : this(pair.first.toFloat(), pair.second.toFloat())

    /**
     * Scale each axis independently
     *
     * `([x] * [scaleX], [y] * [scaleY])`
     */
    fun toScale(scaleX: Number = 1, scaleY: Number = 1) =
        FloatPointByPercent(x * scaleX.toFloat(), y * scaleY.toFloat())

    /**
     * Scalar multiply
     *
     * `(x * n, y * n)`
     */
    operator fun times(n: Number) = FloatPointByPercent(x = x * n.toFloat(), y = y * n.toFloat())

    /**
     * Scalar divide
     *
     * `(x / n, y / n)`
     */
    operator fun div(n: Number) = FloatPointByPercent(x = x / n.toFloat(), y = y / n.toFloat())
}
