package com.example.gymapp.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymapp.domain.ProgressPoint
import com.example.gymapp.domain.formatEstimate
import com.example.gymapp.domain.formatLoad
import com.example.gymapp.domain.formatSessionWhen
import com.example.gymapp.domain.formatSignedEstimate
import com.example.gymapp.domain.formatVolume
import com.example.gymapp.service.RestPhase
import com.example.gymapp.service.RestTimerBus
import com.example.gymapp.ui.appViewModelFactory
import com.example.gymapp.ui.components.ActiveRestBanner
import com.example.gymapp.ui.components.ProgressLineChart
import kotlin.math.abs

@Composable
fun ProgressScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    onOpenWorkout: (Long) -> Unit,
) {
    val viewModel: ProgressViewModel = viewModel(
        key = "progress-$exerciseId",
        factory = appViewModelFactory { ProgressViewModel(it.repository, exerciseId) },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val restPhase by RestTimerBus.phase.collectAsStateWithLifecycle()
    val restVisible = restPhase !is RestPhase.Idle
    val exercise = state.exercise
    val hasEstimate = state.points.any { it.bestE1rm != null }
    var chosen by rememberSaveable { mutableStateOf<String?>(null) }
    val mode = chosen ?: if (hasEstimate) "e1rm" else "reps"
    val chartPoints = if (mode == "e1rm") state.points.filter { it.bestE1rm != null } else state.points

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
            Text(exercise?.name ?: "Tiến bộ", style = MaterialTheme.typography.titleLarge)
        }
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }
        if (exercise == null) {
            Text("Không tìm thấy bài tập.", modifier = Modifier.padding(20.dp))
            return
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .then(if (restVisible) Modifier else Modifier.navigationBarsPadding()),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "${exercise.muscle.label} · ${exercise.repMin}–${exercise.repMax} rep",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val latest = chartPoints.lastOrNull()
                val previous = chartPoints.dropLast(1).lastOrNull()
                if (latest == null) {
                    Text(
                        "Ghi một buổi với bài này để thấy đường tiến bộ.",
                        modifier = Modifier.padding(top = 12.dp),
                    )
                } else if (mode == "e1rm" && latest.bestE1rm != null) {
                    val estimate = latest.bestE1rm
                    Text(
                        formatEstimate(estimate),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("1RM ước tính", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val previousEstimate = previous?.bestE1rm
                    val delta = if (previousEstimate != null) estimate - previousEstimate else null
                    if (delta != null && abs(delta) >= 0.05) {
                        Text(
                            "${formatSignedEstimate(delta)} so với buổi trước",
                            color = if (delta > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    latest.e1rmWeightKg?.let { weight ->
                        Text(
                            "Từ ${formatLoad(weight, exercise.addedLoad)} × ${latest.e1rmReps}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Text(
                        "${latest.bestReps}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("rep tốt nhất buổi gần nhất", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (hasEstimate && state.points.isNotEmpty()) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = mode == "e1rm",
                            onClick = { chosen = "e1rm" },
                            label = { Text("1RM") },
                        )
                        FilterChip(
                            selected = mode == "reps",
                            onClick = { chosen = "reps" },
                            label = { Text("Rep") },
                        )
                    }
                }
            }
            if (chartPoints.size >= 1) {
                item {
                    val values = chartPoints.map { point ->
                        if (mode == "e1rm") point.bestE1rm ?: 0.0 else point.bestReps.toDouble()
                    }
                    ProgressLineChart(
                        values = values,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.semantics {
                            contentDescription = if (mode == "e1rm") "Biểu đồ 1RM ước tính" else "Biểu đồ số rep"
                        },
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            formatSessionWhen(chartPoints.first().startedAt),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (chartPoints.size > 1) {
                            Text(
                                formatSessionWhen(chartPoints.last().startedAt),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            item {
                Text(
                    "Epley: 1RM ≈ tạ × (1 + rep ÷ 30). Ước tính đáng tin nhất ở 1–12 rep.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(state.points.asReversed(), key = { it.sessionId }) { point ->
                HistoryRow(point, exercise.addedLoad)
            }
        }
        if (restVisible) ActiveRestBanner(onOpenSession = onOpenWorkout)
    }
}

@Composable
private fun HistoryRow(point: ProgressPoint, addedLoad: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(formatSessionWhen(point.startedAt), style = MaterialTheme.typography.titleMedium)
            val estimate = point.bestE1rm
            val sourceWeight = point.e1rmWeightKg
            if (estimate != null && sourceWeight != null) {
                Text("${formatLoad(sourceWeight, addedLoad)} × ${point.e1rmReps}")
                Text(
                    "1RM ${formatEstimate(estimate)} · ${point.totalReps} rep · volume ${formatVolume(point.volumeKg)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                Text("${point.bestReps} rep tốt nhất · ${point.totalReps} rep")
            }
        }
    }
}
