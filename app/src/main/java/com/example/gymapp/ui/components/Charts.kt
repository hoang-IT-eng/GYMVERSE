package com.example.gymapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun Sparkline(
    values: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        drawSeries(values, color, stroke = 2.dp.toPx(), dot = 2.5.dp.toPx())
    }
}

@Composable
fun ProgressLineChart(
    values: List<Double>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.fillMaxWidth().height(168.dp)) {
        if (values.isEmpty()) return@Canvas
        val floats = values.map { it.toFloat() }
        val min = floats.min()
        val max = floats.max()
        val span = (max - min).let { if (it < 0.01f) 1f else it }
        val pad = if (max - min < 0.01f) span * 0.5f else span * 0.18f
        val lo = min - pad
        val hi = max + pad
        val top = 8.dp.toPx()
        val bottom = size.height - 8.dp.toPx()
        val grid = color.copy(alpha = 0.18f)
        drawLine(grid, Offset(0f, top), Offset(size.width, top), strokeWidth = 1.dp.toPx())
        drawLine(grid, Offset(0f, bottom), Offset(size.width, bottom), strokeWidth = 1.dp.toPx())
        fun y(v: Float) = bottom - ((v - lo) / (hi - lo)) * (bottom - top)
        fun x(i: Int) = if (floats.size == 1) size.width / 2f else i * size.width / (floats.size - 1)
        if (floats.size > 1) {
            val path = Path()
            floats.forEachIndexed { index, value ->
                val point = Offset(x(index), y(value))
                if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
            }
            drawPath(
                path,
                color,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )
        }
        floats.forEachIndexed { index, value ->
            drawCircle(color, radius = 4.dp.toPx(), center = Offset(x(index), y(value)))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSeries(
    values: List<Float>,
    color: Color,
    stroke: Float,
    dot: Float,
) {
    if (values.isEmpty()) return
    val min = values.min()
    val max = values.max()
    val span = (max - min).let { if (it < 0.01f) 1f else it }
    val pad = if (max - min < 0.01f) 0.5f else span * 0.2f
    val lo = min - pad
    val hi = max + pad
    fun y(v: Float) = size.height - ((v - lo) / (hi - lo)) * size.height
    fun x(i: Int) = if (values.size == 1) size.width / 2f else i * size.width / (values.size - 1)
    if (values.size > 1) {
        val path = Path()
        values.forEachIndexed { index, value ->
            val px = x(index)
            val py = y(value)
            if (index == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        drawPath(path, color, style = Stroke(width = stroke, cap = StrokeCap.Round))
    }
    values.forEachIndexed { index, value ->
        drawCircle(color, radius = dot, center = Offset(x(index), y(value)))
    }
}
