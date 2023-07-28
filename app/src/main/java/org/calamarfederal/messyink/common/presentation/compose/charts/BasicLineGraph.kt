package org.calamarfederal.messyink.common.presentation.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Min
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.AndroidPath
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import org.calamarfederal.messyink.ui.theme.MessyInkTheme

/**
 * Drawn size of a graph
 */
data class GraphSize2d(
    /**
     * the stroke width of the drawn horizontal axis
     */
    val xAxisWidth: Dp = 4.dp,
    /**
     * the stroke width of the drawn vertical axis
     */
    val yAxisWidth: Dp = 4.dp,
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
     * Position by percent
     */
    val yAxisAt: Float? = null,

    /**
     * Position by percent
     */
    val xAxisAt: Float? = null,
    /**
     * the stroke width of the drawn line
     */
    val lineSize: Dp = 4.dp,
    /**
     * the stroke width (i.e. diameter) of a point
     */
    val pointDiameter: Dp = 24.dp,
)

/**
 * Determines the color of a (line) graph
 */
data class GraphColor(
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
     * Color of the box background of the text labels
     */
    val signColor: Color,

    /**
     * Color of the text labels
     */
    val signTextColor: Color,

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
            sign: Color = MaterialTheme.colorScheme.primaryContainer,
            text: Color = MaterialTheme.colorScheme.onPrimaryContainer,
            fill: Color = line.copy(alpha = 0.25f),
        ) = GraphColor(
            axisColor = axis,
            gridColor = grid,
            lineColor = line,
            pointColor = point,
            signColor = sign,
            signTextColor = text,
            fillColor = fill
        )
    }
}

/**
 * Line Graph to represent `y` over `x`
 *
 * [lineGraphPoints] are expected to be distinct data points in order
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BasicLineGraph(
    lineGraphPoints: List<PointByPercent>,
    modifier: Modifier = Modifier,
    contentDescription: String = "Line graph",
    graphSize: GraphSize2d = GraphSize2d(),
    graphColors: GraphColor = GraphColor(),
    pointInfo: (Int) -> String? = { null },
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
) {
    Canvas(
        contentDescription = contentDescription,
        modifier = modifier
            .defaultMinSize(
                minWidth = graphSize.pointDiameter * graphSize.xAxisChunks,
                minHeight = graphSize.pointDiameter * graphSize.yAxisChunks
            )
            .fillMaxSize(),
    ) {
        val adjustedGraphPoints = lineGraphPoints.asSequence()
            .map { it.copy(y = 1 - it.y).toScale(size.width, size.height) }.toList()

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
            }
            for (chunk in 1 until graphSize.yAxisChunks) {
                val y = (size.height / graphSize.yAxisChunks) * chunk
                drawLine(
                    color = graphColors.gridColor,
                    start = Offset(x = 0f, y = y),
                    end = Offset(x = size.width, y = y),
                    strokeWidth = graphSize.yAxisGridWidth.toPx(),
                )
            }
            graphSize.xAxisAt?.let {
                val y = (1 - graphSize.xAxisAt) * size.height
                drawLine(
                    color = graphColors.axisColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = graphSize.xAxisWidth.toPx(),
                )
            }
            graphSize.yAxisAt?.let {
                val x = graphSize.yAxisAt * size.width
                drawLine(
                    color = graphColors.axisColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = graphSize.yAxisWidth.toPx(),
                )
            }
            // Draw line
            drawPoints(
                points = adjustedGraphPoints.asSequence().flatMap { listOf(it, it) }.drop(1)
                    .map { it.toOffset() }.toList(),
                pointMode = PointMode.Lines,
                color = graphColors.lineColor,
                strokeWidth = graphSize.lineSize.toPx(),
            )
            // Draw fill region
            val startPoint = adjustedGraphPoints.firstOrNull()?.toFloat() ?: return@clipRect
            translate(
                left = startPoint.x,
                top = startPoint.y,
            ) {
                val path = AndroidPath()
                adjustedGraphPoints.onEach {
                    path.lineTo(
                        x = it.x.toFloat() - startPoint.x,
                        y = it.y.toFloat() - startPoint.y,
                    )
                }
                val xAxis = (graphSize.xAxisAt?.let { 1f - it } ?: 1f) * size.height - startPoint.y
                path.lineTo(
                    x = adjustedGraphPoints.last().x.toFloat() - startPoint.x,
                    y = xAxis,
                )
                path.lineTo(x = 0f, y = xAxis)
                path.close()
                drawPath(
                    path = path,
                    color = graphColors.fillColor,
                )
            }
        }
        // Draw points
        drawPoints(
            points = adjustedGraphPoints.filterIndexed { index, _ ->
                lineGraphPoints[index].x in 0.00 .. 1.00 && lineGraphPoints[index].y in 0.00 .. 1.00
            }.map { it.toOffset() },
            pointMode = PointMode.Points,
            cap = StrokeCap.Round,
            color = graphColors.pointColor,
            strokeWidth = graphSize.pointDiameter.toPx(),
        )

        // Draw labels
        for (i in lineGraphPoints.indices) {
            val result = textMeasurer.measure(
                text = pointInfo(i) ?: continue,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                maxLines = 1,
            )
            val resultTopLeft = adjustedGraphPoints[i]
                .translate(
                    xDelta = -result.size.width / 2f,
                    yDelta = -result.size.height / 2f,
                )
                .toOffset()
            val verticalPadding: Float = 1.dp.toPx()
            val horizontalPadding: Float = 2.dp.toPx()
            // Background box
            drawRoundRect(
                color = graphColors.signColor,
                topLeft = resultTopLeft + Offset(-horizontalPadding, -verticalPadding),
                size = Size(
                    width = result.size.width.toFloat() + 2 * horizontalPadding,
                    height = result.size.height.toFloat() + 2 * verticalPadding,
                ),
                cornerRadius = CornerRadius(2.dp.toPx()),
            )
            // Text
            drawText(
                textLayoutResult = result,
                topLeft = resultTopLeft,
                color = graphColors.signTextColor,
            )
        }
    }
}

/**
 * Line Graph with slots for labeling
 *
 * [rangeSlotIndexed] and [domainSlotIndexed] provide Zero-indexed labels for gridlines
 */
@Composable
fun LineGraph(
    lineGraphPoints: List<PointByPercent>,
    modifier: Modifier = Modifier,
    graphModifier: Modifier = Modifier,
    contentDescription: String = "Line graph",
    pointInfo: (Int) -> String? = { null },
    title: @Composable () -> Unit = {},
    domainSlot: @Composable () -> Unit = {},
    rangeSlot: @Composable () -> Unit = {},
    rangeSlotIndexed: @Composable (Int) -> Unit = {},
    domainSlotIndexed: @Composable (Int) -> Unit = {},
    size: GraphSize2d = GraphSize2d(),
    colors: GraphColor = GraphColor(),
) {
    Column(modifier = modifier) {
        /**
         * # TITLE
         */
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides LocalTextStyle.current + MaterialTheme.typography.headlineMedium
            ) {
                title()
            }
        }
        /**
         * # Range labels and Graph
         */
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (yAxisLabel, xAxisLabel, graphBox) = createRefs()

            /**
             * ## Graph
             */
            BasicLineGraph(
                lineGraphPoints = lineGraphPoints,
                pointInfo = pointInfo,
                graphSize = size,
                graphColors = colors,
                contentDescription = contentDescription,
                modifier = graphModifier.constrainAs(graphBox) {
                    val radius = size.pointDiameter / 2
                    top.linkTo(parent.top, radius)
                    bottom.linkTo(xAxisLabel.top, radius)
                    start.linkTo(yAxisLabel.end, radius)
                    end.linkTo(parent.end, radius)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                })

            /**
             * ## X Axis label
             */
            Column(modifier = Modifier
                .height(Min)
                .constrainAs(xAxisLabel) {
                    top.linkTo(graphBox.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(graphBox.start)
                    end.linkTo(graphBox.end)
                    width = Dimension.fillToConstraints
                }) {
                Row {
                    CompositionLocalProvider(
                        LocalTextStyle provides LocalTextStyle.current + MaterialTheme.typography.labelMedium
                    ) {
                        for (c in 0 until size.xAxisChunks) {
                            Box(
                                contentAlignment = Alignment.TopCenter,
                                modifier = Modifier.weight(1f)
                            ) {
                                domainSlotIndexed(c)
                            }
                        }
                    }
                }
                Box { domainSlot() }
            }

            /**
             * ## Y Axis label
             */
            Row(modifier = Modifier
                .height(Min)
                .constrainAs(yAxisLabel) {
                    start.linkTo(parent.start)
                    end.linkTo(graphBox.start)
                    top.linkTo(graphBox.top)
                    bottom.linkTo(graphBox.bottom)
                    height = Dimension.fillToConstraints
                }) {
                Box { rangeSlot() }
                Column(horizontalAlignment = Alignment.End) {
                    for (c in size.yAxisChunks - 1 downTo 0) {
                        Box(
                            contentAlignment = Alignment.CenterEnd, modifier = Modifier.weight(1f)
                        ) {
                            CompositionLocalProvider(
                                LocalTextStyle provides LocalTextStyle.current + MaterialTheme.typography.labelMedium
                            ) {
                                rangeSlotIndexed(c)
                            }
                        }
                    }
                }
            }
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
private fun LineGraphAltPreview() {
    MessyInkTheme {
        Surface {
            LineGraph(
                lineGraphPoints = mapOf(
                    0.00 to 1.00,
                    .3 to .2,
                    .5 to .4,
                    1.00 to 0.00,
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
            val points = sequenceOf(
                1.00 to 1.00,
                0.50 to 0.50,
                0.25 to 0.75,
                0.00 to 0.00,
            ).map { PointByPercent(it) }.toList()
            LineGraph(
                lineGraphPoints = points,
                pointInfo = { "${points[it].y}" },
                title = { Text("Title :)") },
                domainSlot = { Text("Domain") },
                domainSlotIndexed = { Text(text = "ddddd$it") },
                rangeSlot = { Text("Range") },
                rangeSlotIndexed = { i -> Text("r$i") },
                modifier = Modifier,
            )
        }
    }
}
