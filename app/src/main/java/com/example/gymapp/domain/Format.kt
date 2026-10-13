package com.example.gymapp.domain

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.round

fun normalizeKg(value: Double): Double = round(value * 100.0) / 100.0

fun formatKg(value: Double): String {
    val rounded = normalizeKg(value)
    val asInt = rounded.toInt()
    if (abs(rounded - asInt) < 0.001) return asInt.toString()
    return String.format(Locale.US, "%.2f", rounded).trimEnd('0').trimEnd('.')
}

fun formatLoad(weightKg: Double, addedLoad: Boolean): String {
    val number = formatKg(weightKg)
    return if (addedLoad) "+$number kg" else "$number kg"
}

fun formatSignedKg(value: Double): String {
    val sign = if (value > 0) "+" else ""
    return sign + formatKg(value)
}

fun formatEstimate(value: Double): String = formatKg(round(value * 10.0) / 10.0)

fun formatSignedEstimate(value: Double): String {
    val magnitude = formatEstimate(abs(value))
    return when {
        value > 0 -> "+$magnitude"
        value < 0 -> "-$magnitude"
        else -> magnitude
    }
}

fun formatVolume(kg: Double): String = "${round(kg).toLong()} kg"

fun formatElapsed(ms: Long): String {
    val totalSec = (ms / 1000).toInt().coerceAtLeast(0)
    val hours = totalSec / 3600
    val minutes = (totalSec % 3600) / 60
    val seconds = totalSec % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}

fun formatSessionWhen(
    epochMs: Long,
    now: Long = System.currentTimeMillis(),
    zone: ZoneId = ZoneId.systemDefault(),
): String {
    val date = Instant.ofEpochMilli(epochMs).atZone(zone).toLocalDate()
    val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
    return when (date) {
        today -> "Hôm nay"
        today.minusDays(1) -> "Hôm qua"
        else -> date.format(DateTimeFormatter.ofPattern("EEE d/M", Locale.forLanguageTag("vi-VN")))
    }
}

fun formatDuration(startMs: Long, endMs: Long): String {
    val minutes = ((endMs - startMs) / 60_000).toInt().coerceAtLeast(1)
    return if (minutes < 60) {
        "$minutes phút"
    } else {
        val hours = minutes / 60
        val rest = minutes % 60
        if (rest == 0) "$hours giờ" else "$hours giờ $rest phút"
    }
}

fun summarizeSets(sets: List<SetPerformance>, addedLoad: Boolean): String {
    if (sets.isEmpty()) return ""
    val parts = mutableListOf<String>()
    var index = 0
    while (index < sets.size) {
        val weight = normalizeKg(sets[index].weightKg)
        val reps = mutableListOf<Int>()
        while (index < sets.size && normalizeKg(sets[index].weightKg) == weight) {
            reps += sets[index].reps
            index++
        }
        parts += "${formatLoad(weight, addedLoad)} × ${reps.joinToString(", ")}"
    }
    return parts.joinToString(" · ")
}
