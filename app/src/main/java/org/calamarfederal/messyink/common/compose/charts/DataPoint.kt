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
interface Point2d<T : Number> {
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
interface Point3d<T : Number> {
    val x: T
    val y: T
    val z: T
}

/**
 * A 2D line segment
 *
 * @property[start] - start point
 * @property[end] - end point
 */
data class LineSegment<T : Number>(
    val start: Point2d<T>,
    val end: Point2d<T>,
)

/**
 * Point in chart defined using it's percentage of the total size
 *
 */
@JvmInline
value class PointByPercent<T : Number>(private val point: Pair<T, T>) : Point2d<T> {
    constructor(x: T, y: T) : this(Pair(x, y))

    override val x: T get() = point.first

    override val y: T get() = point.second
}

/**
 * Generic 2D Point without any specific definition
 */
@JvmInline
value class DataPoint<T : Number> private constructor(private val point: Pair<T, T>) : Point2d<T> {
    constructor(x: T, y: T) : this(Pair(x, y))

    override val x: T get() = point.first
    override val y: T get() = point.second
}

