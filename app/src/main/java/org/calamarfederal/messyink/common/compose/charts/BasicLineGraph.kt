package org.calamarfederal.messyink.common.compose.charts

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.AndroidPath
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

/**
 * Drawn size of a graph
 */
data class GraphSize2d(
    /**
     * the stroke width of the drawn horizontal axis
     */
    val xAxisWidth: Dp = 2.dp,
    /**
     * the stroke width of the drawn vertical axis
     */
    val yAxisWidth: Dp = 2.dp,
    /**
     * the stroke width of the drawn horizontal grid lines
     */
    val xAxisGridWidth: Dp = Dp.Hairline,
    /**
     * the stroke width of the drawn vertical grid lines
     */
    val yAxisGridWidth: Dp = Dp.Hairline,
    /**
     * the number of divisions along the x axis (for grid lines)
     */
    val xAxisChunks: Int = 10,
    /**
     * the number of divisions along the y axis (for grid lines)
     */
    val yAxisChunks: Int = 10,
    /**
     * the stroke width of the drawn line
     */
    val lineSize: Dp = 4.dp,
    /**
     * the stroke width (i.e. diameter) of a point
     */
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
    lineGraphPoints: List<PointByPercent>,
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
                    // Draw line
                    drawPoints(
                        points = lineGraphPoints
                            .asSequence()
                            .flatMap { listOf(it, it) }
                            .drop(1)
                            .map { it.toScale(size.width, size.height).toOffset() }
                            .toList(),
                        pointMode = PointMode.Lines,
                        color = graphColors.lineColor,
                        strokeWidth = graphSize.lineSize.toPx(),
                    )
                    // Draw fill region
                    val startPoint = lineGraphPoints
                        .first()
                        .toScale(size.width, size.height)
                        .toFloat()
                    val totalSize = size
                    translate(
                        left = startPoint.x,
                        top = startPoint.y,
                    ) {
                        val path = AndroidPath()
                        lineGraphPoints.onEach {
                            path.lineTo(
                                x = it.x.toFloat() * totalSize.width - startPoint.x,
                                y = it.y.toFloat() * totalSize.height - startPoint.y,
                            )
                        }
                        path.lineTo(
                            x = lineGraphPoints.last().x.toFloat() * totalSize.width - startPoint.x,
                            y = size.height,
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
                    points = lineGraphPoints
                        .filter { it.x in 0.00 .. 1.00 && it.y in 0.00 .. 1.00 }
                        .map { it.toScale(size.width, size.height).toOffset() },
                    pointMode = PointMode.Points,
                    cap = StrokeCap.Round,
                    color = graphColors.pointColor,
                    strokeWidth = graphSize.pointSize.toPx(),
                )
                drawContent()
            }
        })
}

/**
 * Line Graph with slots for labeling
 *
 * [rangeChunkLabel] and [domainChunkLabel] provide Zero-indexed labels for gridlines
 */
@Composable
fun LineGraph(
    lineGraphPoints: List<PointByPercent>,
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
    Column(modifier = modifier.padding(horizontal = size.pointSize / 2)) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current + MaterialTheme.typography.headlineMedium
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                title()
            }
        }
        BasicLineGraph(
            lineGraphPoints = lineGraphPoints,
            graphSize = size,
            graphColors = colors,
            modifier = graphModifier
                .padding(vertical = size.pointSize / 2) // perfect padding for when points are on the edge
                .fillMaxWidth()
        )
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current + MaterialTheme.typography.titleMedium
        ) {
            Row {
                for (c in 0 until size.xAxisChunks) {
                    domainChunkLabel(c)()
                }
            }
        }
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current + MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            )
        ) {
            domainLabel()
        }
    }
}

@Preview
@Composable
private fun BasicGraphPreview() {
    MessyInkTheme {
        Surface {
            BasicLineGraph(
                lineGraphPoints = mapOf(
                    .3 to .2,
                    .5 to .4,
                ).map { PointByPercent(it.key, it.value) },
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun LabeledGraphPreview() {
    MessyInkTheme {
        Surface {
            LineGraph(
                lineGraphPoints = sequenceOf(
                    1.00 to 1.00,
                    0.50 to 0.50,
                    0.25 to 0.75,
                ).map { PointByPercent(it) }.toList(),
                title = { Text("Title :)") },
                domainLabel = { Text("Domain") },
                domainChunkLabel = { i -> { Text("preview index: $i") } },
                modifier = Modifier,
            )
        }
    }
}

