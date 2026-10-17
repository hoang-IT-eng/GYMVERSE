package com.example.gymapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.gymapp.ui.theme.BurntSecondary
import com.example.gymapp.ui.theme.IronSurface
import com.example.gymapp.ui.theme.IronSurfaceHighest
import com.example.gymapp.ui.theme.NeonPrimary
import com.example.gymapp.ui.theme.NeonPrimaryContainer

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
    secondaryValues: List<Double>? = null,
) {
    Canvas(modifier.fillMaxWidth().height(180.dp)) {
        if (values.isEmpty()) return@Canvas
        val floats = values.map { it.toFloat() }
        val min = floats.min()
        val max = floats.max()
        val span = (max - min).let { if (it < 0.01f) 1f else it }
        val pad = if (max - min < 0.01f) span * 0.5f else span * 0.18f
        val lo = min - pad
        val hi = max + pad
        val top = 12.dp.toPx()
        val bottom = size.height - 12.dp.toPx()
        val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

        // 4 horizontal dashed grid lines like Stitch SVG
        for (i in 0..3) {
            val gy = top + (bottom - top) * (i / 3f)
            drawLine(
                color = IronSurfaceHighest,
                start = Offset(0f, gy),
                end = Offset(size.width, gy),
                strokeWidth = 1.dp.toPx(),
                pathEffect = if (i < 3) dash else null,
            )
        }

        fun y(v: Float) = bottom - ((v - lo) / (hi - lo)) * (bottom - top)
        fun x(i: Int) = if (floats.size == 1) size.width / 2f else i * size.width / (floats.size - 1)

        if (floats.size > 1) {
            val areaPath = Path()
            val linePath = Path()
            floats.forEachIndexed { index, value ->
                val point = Offset(x(index), y(value))
                if (index == 0) {
                    areaPath.moveTo(point.x, point.y)
                    linePath.moveTo(point.x, point.y)
                } else {
                    areaPath.lineTo(point.x, point.y)
                    linePath.lineTo(point.x, point.y)
                }
            }
            areaPath.lineTo(x(floats.lastIndex), bottom)
            areaPath.lineTo(x(0), bottom)
            areaPath.close()

            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(NeonPrimaryContainer.copy(alpha = 0.30f), Color.Transparent),
                    startY = top,
                    endY = bottom,
                ),
            )
            drawPath(
                path = linePath,
                color = color,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )
        }

        // Optional secondary dashed line (RIR trend on Overload Detail screen)
        if (!secondaryValues.isNullOrEmpty() && secondaryValues.size == floats.size && secondaryValues.size > 1) {
            val secPath = Path()
            secondaryValues.forEachIndexed { idx, rir ->
                val norm = (rir.toFloat().coerceIn(0f, 3.5f) / 3.5f)
                val py = bottom - norm * (bottom - top)
                val px = x(idx)
                if (idx == 0) secPath.moveTo(px, py) else secPath.lineTo(px, py)
            }
            drawPath(
                path = secPath,
                color = BurntSecondary,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, pathEffect = dash),
            )
        }

        floats.forEachIndexed { index, value ->
            val center = Offset(x(index), y(value))
            val isLatest = index == floats.lastIndex
            if (isLatest) {
                drawLine(
                    color = NeonPrimaryContainer.copy(alpha = 0.6f),
                    start = center,
                    end = Offset(center.x, bottom),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = dash,
                )
                drawCircle(NeonPrimary.copy(alpha = 0.25f), radius = 10.dp.toPx(), center = center)
                drawCircle(NeonPrimary, radius = 6.dp.toPx(), center = center)
                drawCircle(IronSurface, radius = 2.5.dp.toPx(), center = center)
            } else {
                drawCircle(color, radius = 4.dp.toPx(), center = center)
                drawCircle(IronSurface, radius = 2.dp.toPx(), center = center)
            }
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
