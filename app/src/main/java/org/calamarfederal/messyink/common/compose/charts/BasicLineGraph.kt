package org.calamarfederal.messyink.common.compose.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.AndroidPath
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

/**
 * Drawn size of a graph
 *
 * @property[xAxisWidth] the stroke width of the drawn horizontal axis
 * @property[yAxisWidth] the stroke width of the drawn vertical axis
 * @property[xAxisGridWidth] the stroke width of the drawn horizontal grid lines
 * @property[yAxisGridWidth] the stroke width of the drawn vertical grid lines
 * @property[xAxisChunks] the number of divisions along the x axis (for grid lines)
 * @property[yAxisChunks] the number of divisions along the y axis (for grid lines)
 * @property[lineSize] the stroke width of the drawn line
 * @property[pointSize] the stroke width (i.e. diameter) of a point
 */
data class GraphSize2d(
    val xAxisWidth: Dp = 2.dp,
    val yAxisWidth: Dp = 2.dp,
    val xAxisGridWidth: Dp = Dp.Hairline,
    val yAxisGridWidth: Dp = Dp.Hairline,
    val xAxisChunks: Int = 10,
    val yAxisChunks: Int = 10,
    val lineSize: Dp = 4.dp,
    val pointSize: Dp = 12.dp,
)

/**
 * Determines the color of a (line) graph
 */
data class GraphColor constructor(
    /**
     * Color of every axis
     */
    val axisColor: Color,
    /**
     * Color of the grid lines
     */
    val gridColor: Color,
    /**
     * Color of the line drawn
     */
    val lineColor: Color,
    /**
     * Color of the points on the line drawn
     */
    val pointColor: Color,
    /**
     * Color of filled area
     */
    val fillColor: Color,
) {
    companion object {
        /**
         * Default Material 3 Compliant Implementation
         */
        @Composable
        operator fun invoke(
            axis: Color = MaterialTheme.colorScheme.outlineVariant,
            grid: Color = MaterialTheme.colorScheme.outlineVariant,
            line: Color = MaterialTheme.colorScheme.primary,
            point: Color = MaterialTheme.colorScheme.primary,
            fill: Color = line.copy(alpha = 0.25f),
        ) = GraphColor(
            axisColor = axis,
            gridColor = grid,
            lineColor = line,
            pointColor = point,
            fillColor = fill
        )
    }
}

/**
 * Line Graph to represent `y` over `x`
 *
 * [lineGraphPoints] are expected to be distinct data points in order
 */
@Composable
fun BasicLineGraph(
    lineGraphPoints: List<PointByPercent<*>>,
    modifier: Modifier = Modifier,
    graphSize: GraphSize2d = GraphSize2d(),
    graphColors: GraphColor = GraphColor(),
) {
    Box(modifier = modifier
        .defaultMinSize(
            minWidth = graphSize.pointSize * graphSize.xAxisChunks,
            minHeight = graphSize.pointSize * graphSize.yAxisChunks
        )
        .drawWithCache {
            // Draw Graph
            onDrawWithContent {
                // Draw lines
                clipRect {
                    // Draw Axes
                    drawLine(
                        color = graphColors.axisColor,
                        start = Offset(x = 0f, y = size.height),
                        end = Offset(x = size.width, y = size.height),
                        strokeWidth = graphSize.xAxisWidth.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = graphColors.axisColor,
                        start = Offset(x = 0f, y = 0f),
                        end = Offset(x = 0f, y = size.height),
                        strokeWidth = graphSize.yAxisWidth.toPx(),
                        cap = StrokeCap.Round,
                    )
                    // Draw Grid
                    for (chunk in 1 until graphSize.xAxisChunks) {
                        val x = (size.width / graphSize.xAxisChunks) * chunk
                        drawLine(
                            color = graphColors.gridColor,
                            start = Offset(x = x, y = 0f),
                            end = Offset(x = x, y = size.height),
                            strokeWidth = graphSize.xAxisGridWidth.toPx(),
                        )
                        val y = (size.height / graphSize.yAxisChunks) * chunk
                        drawLine(
                            color = graphColors.gridColor,
                            start = Offset(x = 0f, y = y),
                            end = Offset(x = size.width, y = y),
                            strokeWidth = graphSize.yAxisGridWidth.toPx(),
                        )
                    }
                    drawPoints(
                        points = lineGraphPoints
                            .asSequence()
                            .flatMap { listOf(it, it) }
                            .drop(1)
                            .map {
                                Offset(
                                    x = size.width * it.x.toFloat(),
                                    y = size.height * it.y.toFloat(),
                                )
                            }
                            .toList(),
                        pointMode = PointMode.Lines,
                        color = graphColors.lineColor,
                        strokeWidth = graphSize.lineSize.toPx(),
                    )
                    // Draw fill region
                    val leftOrigin = lineGraphPoints.first().x.toFloat() * size.width
                    val topOrigin = lineGraphPoints.first().y.toFloat() * size.height
                    val totalSize = size
                    inset(
                        left = leftOrigin,
                        top = topOrigin,
                        right = 0f,
                        bottom = 0f,
                    ) {
                        val path = AndroidPath()
                        lineGraphPoints.onEach {
                            path.lineTo(
                                x = it.x.toFloat() * totalSize.width - leftOrigin,
                                y = it.y.toFloat() * totalSize.height - topOrigin,
                            )
                        }
                        path.lineTo(
                            x = lineGraphPoints.last().x.toFloat() * totalSize.width - leftOrigin,
                            y = size.height
                        )
                        path.lineTo(x = 0f, y = size.height)
                        drawPath(
                            path = path,
                            color = graphColors.fillColor,
                        )
                    }
                }
                // Draw points
                drawPoints(
                    points = lineGraphPoints.mapNotNull bounds@{
                        Offset(
                            x = size.width * it.x
                                .toFloat()
                                .also { x -> if (x !in 0f .. 1f) return@bounds null },
                            y = size.height * it.y
                                .toFloat()
                                .also { y -> if (y !in 0f .. 1f) return@bounds null },
                        )
                    },
                    pointMode = PointMode.Points,
                    cap = StrokeCap.Round,
                    color = graphColors.pointColor,
                    strokeWidth = graphSize.pointSize.toPx(),
                )
                drawContent()
            }
        }
    )
}

/**
 * Line Graph with slots for labeling
 *
 * [rangeChunkLabel] and [domainChunkLabel] provide Zero-indexed labels for gridlines
 */
@Composable
fun LineGraph(
    lineGraphPoints: List<PointByPercent<*>>,
    modifier: Modifier = Modifier,
    graphModifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    domainLabel: @Composable () -> Unit = {},
    rangeLabel: @Composable () -> Unit = {},
    rangeChunkLabel: (Int) -> (@Composable () -> Unit) = { _ -> {} },
    domainChunkLabel: (Int) -> (@Composable () -> Unit) = { _ -> {} },
    size: GraphSize2d = GraphSize2d(),
    colors: GraphColor = GraphColor(),
) {
    Column(modifier = modifier) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current + MaterialTheme.typography.titleLarge
        ) {
            title()
        }
        BasicLineGraph(
            lineGraphPoints = lineGraphPoints,
            graphSize = size,
            graphColors = colors,
            modifier = graphModifier
                .padding(size.pointSize / 2) // perfect padding for when points are on the edge
                .fillMaxWidth()
        )
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current + MaterialTheme.typography.labelSmall
        ) {
            for (c in 0 until size.xAxisChunks) domainChunkLabel(c)
        }
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current + MaterialTheme.typography.titleMedium
        ) {
            domainLabel()
        }
    }
}

@Preview
@Composable
private fun GraphPreview() {
    MessyInkTheme {
        Surface {
            BasicLineGraph(
                lineGraphPoints = mapOf(
                    .3f to .2f,
                    .5f to .4f,
                ).map { PointByPercent(it.key, it.value) },
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp),
            )
        }
    }
}

